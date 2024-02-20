import mysql.connector

def get_mysql_connection():
    # Replace these values with your MySQL server details
    host = "153.92.15.5"
    user = "u754009667_viddoer"
    password = "Nitin_Kumar@123"
    database = "u754009667_viddoer"

    try:
        # Establish a connection to the MySQL server
        connection = mysql.connector.connect(
            host=host,
            user=user,
            password=password,
            database=database
        )

        if connection.is_connected():
            print("Connected to MySQL database")
            return connection

    except mysql.connector.Error as e:
        print(f"Error: {e}")

    return None

def create_users_table(connection):
    try:
        # Create a cursor to execute SQL queries
        cursor = connection.cursor()

        # SQL statement to create the 'users' table
        create_table_query = """
            CREATE TABLE users (
                user_id INT AUTO_INCREMENT PRIMARY KEY,
                username VARCHAR(50) NOT NULL,
                email VARCHAR(100) NOT NULL,
                password VARCHAR(255) NOT NULL,
                registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """

        # Execute the SQL statement
        cursor.execute(create_table_query)

        # Commit the changes
        connection.commit()

        print("Table 'users' created successfully")

    except mysql.connector.Error as e:
        print(f"Error: {e}")

    finally:
        # Close the cursor (connection remains open)
        if 'cursor' in locals():
            cursor.close()

# Example usage:
connection = get_mysql_connection()

if connection:
    create_users_table(connection)
    # Perform other database operations here if needed
    connection.close()  # Close the connection when done
