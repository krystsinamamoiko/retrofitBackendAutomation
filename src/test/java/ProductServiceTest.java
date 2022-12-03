import api.ProductService;
import com.github.javafaker.Faker;
import dto.Product;
import okhttp3.ResponseBody;
import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import retrofit2.Response;
import utils.RetrofitUtils;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;

public class ProductServiceTest {

    final static String PRODUCT_CATEGORY = "Food";
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
    @DisplayName("Verify a new product creation")
    void testCreateProduct() throws IOException {
        Response<Product> response = productService.createProduct(product).execute();
        assertThat(response.isSuccessful(), CoreMatchers.is(true));
        assertThat(response.body().getTitle(), CoreMatchers.is(product.getTitle()));
        assertThat(response.body().getPrice(), CoreMatchers.is(product.getPrice()));
        assertThat(response.body().getCategoryTitle(), CoreMatchers.is(product.getCategoryTitle()));
        productId = response.body().getId();

        boolean isProductInList = false;
        Response<List<Product>> responseList = productService.getProducts().execute();

        for(Product item : responseList.body()) {
            if(item.getId().equals(productId) &&
               item.getTitle().equals(product.getTitle()) &&
               item.getCategoryTitle().equals(product.getCategoryTitle()) &&
               item.getPrice().equals(product.getPrice()) ) {
                isProductInList = true;
                break;
            }
        }
        assertThat(isProductInList, CoreMatchers.is(true));
    }

    @Test
    @DisplayName("Verify a product modification")
    void testModifyProduct() throws IOException {
        createProduct(product);

        // modify product properties
        product.setId(productId);
        product.setPrice(faker.random().nextInt(2, 100));
        product.setTitle(faker.food().ingredient());

        Response<Product> responseModify = productService.modifyProduct(product).execute();
        assertThat(responseModify.isSuccessful(), CoreMatchers.is(true));
        assertThat(responseModify.body().getTitle(), CoreMatchers.is(product.getTitle()));
        assertThat(responseModify.body().getPrice(), CoreMatchers.is(product.getPrice()));
        assertThat(responseModify.body().getCategoryTitle(), CoreMatchers.is(product.getCategoryTitle()));
        assertThat(responseModify.body().getId(), CoreMatchers.is(productId));

        Response<Product> response = productService.getProductById(productId).execute();
        assertThat(response.isSuccessful(), CoreMatchers.is(true));
        assertThat(response.body().getTitle(), CoreMatchers.is(product.getTitle()));
        assertThat(response.body().getPrice(), CoreMatchers.is(product.getPrice()));
        assertThat(response.body().getCategoryTitle(), CoreMatchers.is(product.getCategoryTitle()));
    }

    @Test
    @DisplayName("Verify product list get")
    void testProductListGet() throws IOException {
        createProduct(product);

        Response<List<Product>> responseList = productService.getProducts().execute();
        assertThat(responseList.isSuccessful(), CoreMatchers.is(true));
        assertThat(responseList.body().size(), Matchers.greaterThan(0));
    }

    @Test
    @DisplayName("Verify a product get by id")
    void testProductGetById() throws IOException {
        createProduct(product);
        product.setId(productId);

        Response<Product> response = productService.getProductById(productId).execute();
        assertThat(response.isSuccessful(), CoreMatchers.is(true));
        assertThat(response.body().getTitle(), CoreMatchers.is(product.getTitle()));
        assertThat(response.body().getPrice(), CoreMatchers.is(product.getPrice()));
        assertThat(response.body().getCategoryTitle(), CoreMatchers.is(product.getCategoryTitle()));
    }

    @AfterEach
    void tearDown() throws IOException {
        if (productId != 0) {
            Response<ResponseBody> response = productService.deleteProduct(productId).execute();
            assertThat(response.isSuccessful(), CoreMatchers.is(true));
        }
    }

    private void createProduct(Product product) throws IOException {
        Response<Product> responseCreate = productService.createProduct(product).execute();
        assertThat(responseCreate.isSuccessful(), CoreMatchers.is(true));
        assertThat(responseCreate.body().getId(), CoreMatchers.notNullValue());
        productId = responseCreate.body().getId();
    }
}
