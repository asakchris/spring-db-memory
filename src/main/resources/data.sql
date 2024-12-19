-- Create table if not exists
CREATE TABLE IF NOT EXISTS data_table (
    id BIGINT PRIMARY KEY,
    json_data JSONB
);

-- Insert sample data
INSERT INTO data_table (id, json_data) VALUES (
    1,
    '{
        "name": "John Doe",
        "age": 30,
        "address": {
            "street": "123 Main St",
            "city": "New York",
            "country": "USA"
        },
        "contacts": [
            {
                "type": "email",
                "value": "john@example.com"
            },
            {
                "type": "phone",
                "value": "+1-555-123-4567"
            }
        ]
    }'::jsonb
); 