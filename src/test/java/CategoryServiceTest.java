import api.CategoryService;
import dto.Category;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import retrofit2.Response;
import utils.RetrofitUtils;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class CategoryServiceTest extends AbstractTest {

    static CategoryService categoryService;
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

        getCategoriesModel().createCriteria().andIdEqualTo(Long.valueOf(CATEGORY_ID));
        List<model.Categories> list = getCategoriesMapper().selectByExample(getCategoriesModel());
        assertThat(list.size(), equalTo(1));
        assertThat(response.body().getTitle(), equalTo(list.get(0).getTitle()));
        response.body().getProducts().forEach(product -> assertThat(product.getCategoryTitle(), equalTo(list.get(0).getTitle())));
    }
}
