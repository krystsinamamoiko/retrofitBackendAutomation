import api.ProductService;
import com.github.javafaker.Faker;
import dto.Product;
import model.Products;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.*;
import retrofit2.Response;
import utils.RetrofitUtils;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class ProductServiceTest extends AbstractTest {

    final static String PRODUCT_CATEGORY = "Food";
    final static long PRODUCT_CATEGORY_ID = 1L;
    static ProductService productService;

    Product product = null;
    Faker faker = new Faker();
    int productId = 0;

    @BeforeAll
    static void beforeAll() throws IOException {
        productService = RetrofitUtils.getRetrofit().create(ProductService.class);
    }

    @BeforeEach
    void setUp() {
        product = new Product()
            .withTitle(faker.food().ingredient())
            .withCategoryTitle(PRODUCT_CATEGORY)
            .withPrice(faker.random().nextInt(2, 100));
    }

    @Test
    @DisplayName("Verify create a new product endpoint")
    void testCreateProduct() throws IOException {
        Response<Product> response = productService.createProduct(product).execute();
        assertThat(response.isSuccessful(), equalTo(true));
        assertThat(response.body().getTitle(), equalTo(product.getTitle()));
        assertThat(response.body().getPrice(), equalTo(product.getPrice()));
        assertThat(response.body().getCategoryTitle(), equalTo(product.getCategoryTitle()));
        productId = response.body().getId();

        model.Products dbProduct = getProductByIdViaDatabase(productId);
        assertThat(dbProduct, notNullValue());
        assertThat(response.body().getTitle(), equalTo(dbProduct.getTitle()));
        assertThat(response.body().getPrice(), equalTo(dbProduct.getPrice()));
        model.Categories dbCategory = getCategoryByIdViaDatabase(dbProduct.getCategoryId());
        assertThat(response.body().getCategoryTitle(), equalTo(dbCategory.getTitle()));
    }

    @Test
    @DisplayName("Verify modify the given product endpoint")
    void testModifyProduct() throws IOException {
        productId = (int) createProductViaDatabase();

        // modify product properties
        product.setId(productId);
        product.setPrice(faker.random().nextInt(2, 100));
        product.setTitle(faker.food().ingredient());

        Response<Product> responseModify = productService.modifyProduct(product).execute();
        assertThat(responseModify.isSuccessful(), equalTo(true));
        assertThat(responseModify.body().getTitle(), equalTo(product.getTitle()));
        assertThat(responseModify.body().getPrice(), equalTo(product.getPrice()));
        assertThat(responseModify.body().getCategoryTitle(), equalTo(product.getCategoryTitle()));
        assertThat(responseModify.body().getId(), equalTo(productId));

        model.Products dbProduct = getProductByIdViaDatabase(productId);
        assertThat(dbProduct, notNullValue());
        assertThat(responseModify.body().getTitle(), equalTo(dbProduct.getTitle()));
        assertThat(responseModify.body().getPrice(), equalTo(dbProduct.getPrice()));
        model.Categories dbCategory = getCategoryByIdViaDatabase(dbProduct.getCategoryId());
        assertThat(responseModify.body().getCategoryTitle(), equalTo(dbCategory.getTitle()));
    }

    @Test
    @DisplayName("Verify get product list endpoint")
    void testProductListGet() throws IOException {
        Response<List<Product>> responseList = productService.getProducts().execute();
        assertThat(responseList.isSuccessful(), equalTo(true));

        getProductsModel().clear();
        List<model.Products> productList = getProductsMapper().selectByExample(getProductsModel());

        assertThat(responseList.body().size(), equalTo(productList.size()));
        List<String> titlesDB = productList.stream().map(s -> s.getTitle()).sorted().collect(Collectors.toList());
        List<String> titlesAPI = responseList.body().stream().map(s -> s.getTitle()).sorted().collect(Collectors.toList());
        assertThat(titlesDB, equalTo(titlesAPI));
    }

    @Test
    @DisplayName("Verify get the given product by id endpoint")
    void testProductGetById() throws IOException {
        productId = (int) createProductViaDatabase();
        product.setId(productId);

        Response<Product> response = productService.getProductById(productId).execute();
        assertThat(response.isSuccessful(), equalTo(true));
        assertThat(response.body().getId(), equalTo(product.getId()));
        assertThat(response.body().getTitle(), equalTo(product.getTitle()));
        assertThat(response.body().getPrice(), equalTo(product.getPrice()));
        assertThat(response.body().getCategoryTitle(), equalTo(product.getCategoryTitle()));

        model.Products dbProduct = getProductByIdViaDatabase(productId);
        assertThat(dbProduct, notNullValue());
        assertThat(response.body().getTitle(), equalTo(dbProduct.getTitle()));
        assertThat(response.body().getPrice(), equalTo(dbProduct.getPrice()));
        model.Categories dbCategory = getCategoryByIdViaDatabase(dbProduct.getCategoryId());
        assertThat(response.body().getCategoryTitle(), equalTo(dbCategory.getTitle()));
    }

    @Test
    @DisplayName("Verify delete the given product endpoint")
    void testDeleteProduct() throws IOException {
        productId = (int) createProductViaDatabase();

        Response<ResponseBody> response = productService.deleteProduct(productId).execute();
        assertThat(response.isSuccessful(), equalTo(true));

        model.Products dbProduct = getProductByIdViaDatabase(productId);
        assertThat(dbProduct, nullValue());
        productId = 0;
    }

    @AfterEach
    void tearDown() {
        if (productId != 0) {
            getProductsModel().clear();
            getProductsModel().createCriteria().andIdEqualTo(Long.valueOf(productId));
            getProductsMapper().deleteByExample(getProductsModel());
            getSession().commit();
        }
    }

    private long createProductViaDatabase() {
        model.Products products = new Products();
        products.setPrice(product.getPrice());
        products.setTitle(product.getTitle());
        products.setCategoryId(PRODUCT_CATEGORY_ID);
        getProductsMapper().insert(products);
        getSession().commit();

        getProductsModel().clear();
        getProductsModel()
            .createCriteria()
            .andTitleEqualTo(products.getTitle())
            .andCategoryIdEqualTo(PRODUCT_CATEGORY_ID)
            .andPriceEqualTo(products.getPrice());
        List<Products> productList = getProductsMapper().selectByExample(getProductsModel());

        if(productList.size() >= 1) {
            return productList.get(0).getId();
        } else {
            return 0;
        }
    }

    private model.Products getProductByIdViaDatabase(long id) {
        getProductsModel().clear();
        getProductsModel().createCriteria().andIdEqualTo(id);
        List<model.Products> productList = getProductsMapper().selectByExample(getProductsModel());
        if(productList.size() >= 1) {
            return productList.get(0);
        } else {
            return null;
        }
    }

    private model.Categories getCategoryByIdViaDatabase(long id) {
        getCategoriesModel().createCriteria().andIdEqualTo(id);
        List<model.Categories> categoryList = getCategoriesMapper().selectByExample(getCategoriesModel());
        assertThat("Category is not found!", categoryList.size(), equalTo(1));
        return categoryList.get(0);
    }
}
