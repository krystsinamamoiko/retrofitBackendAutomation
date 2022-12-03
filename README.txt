Test check-list (all 5 endpoints are involved):
1. Create a new product (CREATE):
 - verify status code
 - verify response body (a new product with corresponding properties is created)
 - verify that a new product is available in full product list (READ)
2. Modify product (UPDATE)
 - create a new product (CREATE)
 - modify this product (UPDATE)
 - verify status code
 - verify body (all properties have corresponding new values)
 - get a specific product by their identifier (READ)
 - verify body (the given product has modified properties values that correspond to new properties' values)
3. Get full list of products
- verify status code
- verify that this list is not empty (a new product should be added in advance)
4. Get a product with the given id
- verify status code
- verify that correct data are returned (data should correspond to the given product properties)
5. Delete product (DELETE) (as a tearDown() method to clear test data after each test)
