
# ShopSphere Database Design

## 1. Introduction

This document describes the database design for the ShopSphere Enterprise Commerce Platform.

ShopSphere uses PostgreSQL as its relational database and Spring Data JPA for database access.

The database design focuses on:

- Data integrity and consistency.
- Clear relationships between entities.
- Maintainable database structures.
- Efficient data retrieval through appropriate indexing.
- Support for future e-commerce features.

## 2. Database Technology

| Component | Technology |
|---|---|
| Database | PostgreSQL |
| Persistence | Spring Data JPA |
| ORM | Hibernate |
| Schema Migration | Flyway |
| Database Port | 5433 |
| Database Name | shopsphere |

Database schema changes must be introduced through versioned Flyway migrations.

Existing migrations must not be modified after they have been applied to a shared or persistent database.

## 3. Database Design Principles

ShopSphere follows these principles:

- Use primary keys to identify records.
- Use foreign keys to maintain referential integrity.
- Use unique constraints for values that must not be duplicated.
- Use database constraints to prevent invalid data.
- Use appropriate indexes for frequently queried columns.
- Store monetary values using NUMERIC rather than floating-point types.
- Keep database entities separate from API request and response DTOs.
- Introduce new schema changes incrementally through Flyway.

## 4. Product Module

The initial Product Module consists of three tables:

1. `products`
2. `categories`
3. `product_categories`

The `products` table stores product information.

The `categories` table stores product category information.

The `product_categories` table connects products and categories.

## 5. Entity Relationship

```text
+------------------+
|     products     |
+------------------+
| id (PK)          |
| name             |
| slug (UNIQUE)    |
| description      |
| sku (UNIQUE)     |
| price            |
| stock_quantity   |
| status           |
+------------------+
         |
         | 1
         |
         | N
+------------------------+
|   product_categories   |
+------------------------+
| product_id (PK, FK)    |
| category_id (PK, FK)   |
+------------------------+
         |
         | N
         |
         | 1
+------------------+
|    categories    |
+------------------+
| id (PK)          |
| name             |
| slug (UNIQUE)    |
| description      |
| status           |
+------------------+
```

A product can belong to multiple categories, and a category can contain multiple products.

The many-to-many relationship is implemented using the `product_categories` mapping table.

## 6. Categories Table

Table name: `categories`

| Column | PostgreSQL Type | Constraints |
|---|---|---|
| id | BIGINT | Primary key, generated identity |
| name | VARCHAR(100) | Not null |
| slug | VARCHAR(120) | Not null, unique |
| description | TEXT | Nullable |
| status | VARCHAR(20) | Not null |
| created_at | TIMESTAMP | Not null, default current timestamp |
| updated_at | TIMESTAMP | Not null, default current timestamp |

### Category Rules

- Category names are required.
- Category slugs must be unique.
- Status values will initially be `ACTIVE` or `INACTIVE`.
- A category referenced by existing products must not be deleted accidentally.
- Category status and deletion behavior must be enforced by the application and database design.

## 7. Products Table

Table name: `products`

| Column | PostgreSQL Type | Constraints |
|---|---|---|
| id | BIGINT | Primary key, generated identity |
| name | VARCHAR(150) | Not null |
| slug | VARCHAR(180) | Not null, unique |
| description | TEXT | Nullable |
| sku | VARCHAR(50) | Not null, unique |
| price | NUMERIC(12,2) | Not null, non-negative |
| stock_quantity | INTEGER | Not null, non-negative |
| status | VARCHAR(20) | Not null |
| created_at | TIMESTAMP | Not null, default current timestamp |
| updated_at | TIMESTAMP | Not null, default current timestamp |

### Product Rules

- Product name and SKU are required.
- Product slugs must be unique.
- SKU identifies a stock-keeping product record.
- Price must not be negative.
- Stock quantity must not be negative.
- Status values will initially be `ACTIVE` or `INACTIVE`.
- Inactive products should not appear in normal customer-facing product listings.
- Product validation must be implemented in both the application layer and database where appropriate.

## 8. Product Categories Mapping Table

Table name: `product_categories`

| Column | PostgreSQL Type | Constraints |
|---|---|---|
| product_id | BIGINT | Not null, foreign key |
| category_id | BIGINT | Not null, foreign key |

### Relationship Constraints

- `product_id` references `products(id)`.
- `category_id` references `categories(id)`.
- The combination of `product_id` and `category_id` must be unique.
- A composite primary key on `(product_id, category_id)` prevents duplicate mappings.
- Foreign-key deletion behavior must be defined explicitly.

## 9. Indexing Strategy

The initial indexing strategy includes:

- Primary-key indexes on all tables.
- Unique indexes for product SKU, product slug and category slug.
- A composite primary-key index on `product_categories(product_id, category_id)`.
- An additional index on `product_categories(category_id, product_id)` for category-to-product lookups.

Additional indexes should be introduced only when justified by query patterns and execution-plan analysis.

Indexes improve some read operations but add storage and write overhead.

## 10. Data Integrity

The database must enforce:

- Primary-key uniqueness.
- Foreign-key relationships.
- Required values through NOT NULL constraints.
- Unique product SKUs and slugs.
- Non-negative product prices and stock quantities.
- Unique product-category mappings.

Application-level validation will provide clear API errors, while database constraints provide a final layer of protection.

## 11. Migration Strategy

Flyway manages database schema changes.

Existing migrations:

- V1: Create users table.
- V2: Create refresh tokens table.

The Product Module schema will be introduced through a new migration:

- V3: Create categories, products and product_categories tables.

The exact migration filename and SQL must be verified against the existing project before implementation.

Applied migrations must not be edited to introduce new features.

## 12. Future Extensions

The database may later be extended to support:

- Product images.
- Product variants.
- Inventory history.
- Product reviews and ratings.
- Hierarchical categories.
- Product search optimization.
- Seller-specific product listings.

These features are outside the initial Product Module schema and will be designed when their requirements are introduced.

## 13. Design Decisions

| Decision | Reason |
|---|---|
| PostgreSQL | Relational data storage and integrity |
| Flyway | Versioned and repeatable schema evolution |
| Separate categories table | Independent category management |
| Mapping table for categories | Supports many-to-many relationships |
| NUMERIC for price | Exact decimal monetary representation |
| Foreign keys and constraints | Protect data integrity |
| Indexes based on query needs | Balance read performance and write overhead |

## 14. Summary

The initial Product Module database design provides structured storage for products and categories while maintaining referential integrity.

The schema follows the existing ShopSphere layered architecture and will be implemented incrementally using Flyway migrations.

Database performance will be evaluated through actual query patterns and execution plans rather than assuming that additional complexity automatically improves performance.
