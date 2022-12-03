import api.CategoryService;
import dto.Category;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import retrofit2.Response;
import utils.RetrofitUtils;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class CategoryServiceTest {

    static CategoryService categoryService;
    final static String PRODUCT_CATEGORY = "Food";
    final static int CATEGORY_ID = 1;

    @BeforeAll
    static void beforeAll() throws IOException {
        categoryService = RetrofitUtils.getRetrofit().create(CategoryService.class);
    }

    @Test
    void getCategoryByIdPositiveTest() throws IOException {
        Response<Category> response = categoryService.getCategory(CATEGORY_ID).execute();

        assertThat(response.isSuccessful(), CoreMatchers.is(true));
        assertThat(response.body().getId(), equalTo(CATEGORY_ID));
        assertThat(response.body().getTitle(), equalTo(PRODUCT_CATEGORY));
        response.body().getProducts().forEach(product -> assertThat(product.getCategoryTitle(), equalTo(PRODUCT_CATEGORY)));
    }
}
