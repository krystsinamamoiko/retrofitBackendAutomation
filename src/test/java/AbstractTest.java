import dao.CategoriesMapper;
import dao.ProductsMapper;
import model.CategoriesExample;
import model.ProductsExample;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.io.IOException;
import java.io.InputStream;

public class AbstractTest {

    private static SqlSession session = null;
    private static dao.ProductsMapper productsMapper;
    private static model.ProductsExample productsModel;
    private static dao.CategoriesMapper categoriesMapper;
    private static model.CategoriesExample categoriesModel;

    @BeforeAll
    static void setUpDatabaseSession() throws IOException {
        String resource = "mybatis-config.xml";
        InputStream inputStream = Resources.getResourceAsStream(resource);
        SqlSessionFactory sqlSessionFactory = new
            SqlSessionFactoryBuilder().build(inputStream);
        session = sqlSessionFactory.openSession();

        categoriesMapper = getSession().getMapper(dao.CategoriesMapper.class);
        categoriesModel = new model.CategoriesExample();

        productsMapper = getSession().getMapper(dao.ProductsMapper.class);
        productsModel = new model.ProductsExample();
    }

    @AfterAll
    static void tearDownDatabaseSession() throws IOException {
        if(session != null) {
            session.close();
        }
    }

    public static SqlSession getSession() {
        return session;
    }

    public static ProductsMapper getProductsMapper() {
        return productsMapper;
    }

    public static ProductsExample getProductsModel() {
        return productsModel;
    }

    public static CategoriesMapper getCategoriesMapper() {
        return categoriesMapper;
    }

    public static CategoriesExample getCategoriesModel() {
        return categoriesModel;
    }
}
