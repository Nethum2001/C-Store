package com.cstore.daos.product;

import com.cstore.models.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ProductDAOImpl implements ProductDAO {
    String url = "jdbc:mysql://localhost:3306/cstore";
    String username = "cadmin";
    String password = "cstore_GRP28_CSE21";

    Logger logger = LoggerFactory.getLogger(ProductDAOImpl.class);

    @Override
    public Optional<Product> findProduct(Product unknown) throws SQLException {
        String sql = "SELECT * " +
                     "FROM `product` " +
                     "WHERE `product_name` = ? AND `base_price` = ? AND `brand` = ? AND `description` = ? AND `main_image` = ?;";

        Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        preparedStatement.setString(1, unknown.getProductName());
        preparedStatement.setBigDecimal(2, unknown.getBasePrice());
        preparedStatement.setString(3, unknown.getBrand());
        preparedStatement.setString(4, unknown.getDescription());
        preparedStatement.setBytes(5, unknown.getMainImage());

        ResultSet resultSet = preparedStatement.executeQuery();
        if (resultSet.next()) {
            unknown.setProductId(resultSet.getLong("category_id"));

            return Optional.of(unknown);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public List<Product> findAll() {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            connection = DriverManager.getConnection(url, username, password);
            List<Product> products = new ArrayList<Product>();
            String sql = "SELECT * " +
                    "FROM `product`;";

            statement = connection.createStatement();
            resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                Product product = new Product();

                product.setProductId(resultSet.getLong("product_id"));
                product.setProductName(resultSet.getString("product_name"));
                product.setBasePrice(resultSet.getBigDecimal("base_price"));
                product.setBrand(resultSet.getString("brand"));
                product.setDescription(resultSet.getString("description"));
                product.setMainImage(resultSet.getBytes("main_image"));

                products.add(product);
            }

            return products;
        }
        catch (SQLException sqe) {
            logger.error("Error while fetching data.", sqe);
            return new ArrayList<>();
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    logger.error("Error closing Result Set.", e);
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    logger.error("Error closing Statement.", e);
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    logger.error("Error closing Connection.", e);
                }
            }
        }
    }

    @Override
    public Optional<Product> findById(Long productId) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DriverManager.getConnection(url, username, password);
            Product product = new Product();
            String sql = "SELECT * " +
                    "FROM product " +
                    "WHERE product_id = ?;";

            preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setLong(1, productId);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                product.setProductId(resultSet.getLong("product_id"));
                product.setProductName(resultSet.getString("product_name"));
                product.setBasePrice(resultSet.getBigDecimal("base_price"));
                product.setBrand(resultSet.getString("brand"));
                product.setDescription(resultSet.getString("description"));
                product.setMainImage(resultSet.getBytes("main_image"));

                resultSet.close();
                preparedStatement.close();
                connection.close();
                return Optional.of(product);
            } else {
                resultSet.close();
                preparedStatement.close();
                connection.close();
                return Optional.empty();
            }
        } catch (SQLException sqe) {
            logger.error("Error while fetching data.", sqe);
            return Optional.empty();
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    logger.error("Error closing Result Set.", e);
                }
            }
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    logger.error("Error closing Prepared Statement.", e);
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    logger.error("Error closing Connection.", e);
                }
            }
        }
    }

    @Override
    public List<Product> findByName(String productName) throws SQLException {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * " +
                     "FROM `product` " +
                     "WHERE `product_name` LIKE ?;";

        Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        preparedStatement.setString(1, "%" + productName + "%");
        ResultSet resultSet = preparedStatement.executeQuery();

        while (resultSet.next()) {
            Product product = new Product();

            product.setProductId(resultSet.getLong("product_id"));
            product.setProductName(resultSet.getString("product_name"));
            product.setBasePrice(resultSet.getBigDecimal("base_price"));
            product.setBrand(resultSet.getString("brand"));
            product.setDescription(resultSet.getString("description"));
            product.setMainImage(resultSet.getBytes("main_image"));

            products.add(product);
        }

        resultSet.close();
        preparedStatement.close();
        connection.close();

        return products;
    }

    @Override
    public void save(Product product) throws SQLException {
        String sql = "INSERT INTO `product`(`product_name`, `base_price`, `brand`, `description`, `main_image`) VALUES(?, ?, ?, ?, ?);";

        Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        preparedStatement.setString(1, product.getProductName());
        preparedStatement.setBigDecimal(2, product.getBasePrice());
        preparedStatement.setString(3, product.getBrand());
        preparedStatement.setString(4, product.getDescription());
        preparedStatement.setBytes(5, product.getMainImage());

        preparedStatement.executeUpdate();

        preparedStatement.close();
        connection.close();
    }

    @Override
    public List<Product> findAllByCategoryId(Long categoryId) throws SQLException {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT `product_id`, `product_name`, `base_price`, `brand`, `description`, `main_image` " +
                     "FROM `product` NATURAL RIGHT OUTER JOIN `belongs_to` " +
                     "WHERE `category_id` = ?;";

        Connection connection = DriverManager.getConnection(url, username, password);
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setLong(1, categoryId);

        ResultSet resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            Product product = new Product();

            product.setProductId(resultSet.getLong("product_id"));
            product.setProductName(resultSet.getString("product_name"));
            product.setBasePrice(resultSet.getBigDecimal("base_price"));
            product.setBrand(resultSet.getString("brand"));
            product.setDescription(resultSet.getString("description"));
            product.setMainImage(resultSet.getBytes("main_image"));

            products.add(product);
        }

        resultSet.close();
        preparedStatement.close();
        connection.close();
        return products;
    }
}