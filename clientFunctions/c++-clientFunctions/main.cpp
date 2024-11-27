#include <iostream>
#include "json.hpp"

#include "ApiClient.h"

int main() {
    try {
        // Initialize ApiClient
        ApiClient apiClient("http://localhost:8080");

        // Example usage of execQuery
        nlohmann::json queryResult = apiClient.execQuery("SELECT * FROM PEOPLE");
        if (!queryResult.is_null()) {
            std::cout << "Query Result:\n" << queryResult.dump(4) << std::endl;
        }

        // Example usage of listTables
        // nlohmann::json tables = apiClient.listTables();
        // if (!tables.is_null()) {
        //     std::cout << tables["data"].get<std::string>() << std::endl;
        // }

        // Example usage of insert
        // bool insertResult = apiClient.insert("PEOPLE", "[10, \"Doe\", \"John\", \"Renner Rd\", \"Dallas\"]");
        // std::cout << "Insert Result: " << (insertResult ? "Success" : "Failure") << std::endl;

        // Example usage of delete
        bool deleteResult = apiClient.deleteEntry("PEOPLE", "[\"FirstName\"]", "[\"John\"]");
        std::cout << "Delete Result: " << (deleteResult ? "Success" : "Failure") << std::endl;

        // Example usage of select
        // nlohmann::json selectResult = apiClient.select("PEOPLE", "[\"firstname\", \"address\"]", "firstname LIKE ?", "[\"A%\"]");
        // if (!selectResult.is_null()) {
        //     std::cout << "Select Result:\n" << selectResult.dump(4) << std::endl;
        // }
    } catch (const std::exception& e) {
        std::cerr << "Error: " << e.what() << std::endl;
    }

    return 0;
}

