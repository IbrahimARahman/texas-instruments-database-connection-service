#include <iostream>
#include <string>
#include <sstream>
#include <curl/curl.h>
#include <json/json.h>

class ApiClient {
private:
    CURL* curl = nullptr;                      // CURL handle for HTTP requests
    struct curl_slist* headers = nullptr;      // HTTP headers
    std::string responseBuffer;                // Buffer to store the response

    // Callback function for handling response data
    static size_t writeCallback(void* contents, size_t size, size_t nmemb, void* userp) {
        ((std::string*)userp)->append((char*)contents, size * nmemb);
        return size * nmemb;
    }

    // Helper function to parse JSON
    Json::Value parseJsonResponse(const std::string& response) {
        Json::CharReaderBuilder reader;
        Json::Value jsonResponse;
        std::string errors;
        std::istringstream stream(response);
        if (!Json::parseFromStream(reader, stream, &jsonResponse, &errors)) {
            throw std::runtime_error("Failed to parse JSON response: " + errors);
        }
        return jsonResponse;
    }

public:
    // Constructor: Initialize libcurl and set up reusable components
    ApiClient() {
        curl = curl_easy_init();
        if (!curl) {
            throw std::runtime_error("Failed to initialize curl.");
        }

        // Set common curl options
        curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, ApiClient::writeCallback);
        curl_easy_setopt(curl, CURLOPT_WRITEDATA, &responseBuffer);

        // Set the Content-Type header to text/plain
        headers = curl_slist_append(headers, "Content-Type: application/JSON");
        curl_easy_setopt(curl, CURLOPT_HTTPHEADER, headers);
    }

    // Destructor: Clean up resources
    ~ApiClient() {
        if (curl) {
            curl_easy_cleanup(curl);
        }
        if (headers) {
            curl_slist_free_all(headers);
        }
    }

    // Function to execute a query by sending a POST request to the API
    Json::Value executeQuery(const std::string& query) {
        if (!curl) {
            throw std::runtime_error("CURL is not initialized.");
        }

        std::string url = "http://localhost:8080/api/execQuery";
        curl_easy_setopt(curl, CURLOPT_URL, url.c_str());
        curl_easy_setopt(curl, CURLOPT_POSTFIELDS, query.c_str());
        curl_easy_setopt(curl, CURLOPT_POSTFIELDSIZE, query.length());

        responseBuffer.clear();
        CURLcode res = curl_easy_perform(curl);
        if (res != CURLE_OK) {
            throw std::runtime_error(curl_easy_strerror(res));
        }

        return parseJsonResponse(responseBuffer);
    }

    // Function to perform a GET request to /listTables
    Json::Value listTables() {
        if (!curl) {
            throw std::runtime_error("CURL is not initialized.");
        }

        std::string url = "http://localhost:8080/api/listTables";
        curl_easy_setopt(curl, CURLOPT_URL, url.c_str());
        curl_easy_setopt(curl, CURLOPT_HTTPGET, 1L);

        responseBuffer.clear();
        CURLcode res = curl_easy_perform(curl);
        if (res != CURLE_OK) {
            throw std::runtime_error(curl_easy_strerror(res));
        }

        return parseJsonResponse(responseBuffer);
    }

    // Function to perform an INSERT operation
    Json::Value insert(const std::string& tableName, const std::string& values) {
        if (!curl) {
            throw std::runtime_error("CURL is not initialized.");
        }

        std::string jsonData = "{\\"tableName\\": \\"" + tableName + "\\", \\"values\\": " + values + "}";
        std::string url = "http://localhost:8080/api/insert";
        curl_easy_setopt(curl, CURLOPT_URL, url.c_str());
        curl_easy_setopt(curl, CURLOPT_POSTFIELDS, jsonData.c_str());
        curl_easy_setopt(curl, CURLOPT_POSTFIELDSIZE, jsonData.length());

        responseBuffer.clear();
        CURLcode res = curl_easy_perform(curl);
        if (res != CURLE_OK) {
            throw std::runtime_error(curl_easy_strerror(res));
        }

        return parseJsonResponse(responseBuffer);
    }

    // Function to perform a DELETE operation
    Json::Value deleteData(const std::string& tableName, const std::string& columns, const std::string& values) {
        if (!curl) {
            throw std::runtime_error("CURL is not initialized.");
        }

        std::string jsonData = "{\\"tableName\\": \\"" + tableName + "\\", \\"columns\\": " + columns + ", \\"values\\": " + values + "}";
        std::string url = "http://localhost:8080/api/delete";
        curl_easy_setopt(curl, CURLOPT_URL, url.c_str());
        curl_easy_setopt(curl, CURLOPT_POSTFIELDS, jsonData.c_str());
        curl_easy_setopt(curl, CURLOPT_POSTFIELDSIZE, jsonData.length());

        responseBuffer.clear();
        CURLcode res = curl_easy_perform(curl);
        if (res != CURLE_OK) {
            throw std::runtime_error(curl_easy_strerror(res));
        }

        return parseJsonResponse(responseBuffer);
    }

    // Function to perform a SELECT operation
    Json::Value select(const std::string& tableName, const std::string& columns, const std::string& whereClause, const std::string& params) {
        if (!curl) {
            throw std::runtime_error("CURL is not initialized.");
        }

        std::string jsonData = "{\\"tableName\\": \\"" + tableName + "\\", \\"columns\\": " + columns + ", \\"whereClause\\": \\"" + whereClause + "\\", \\"params\\": " + params + "}";
        std::string url = "http://localhost:8080/api/select";
        curl_easy_setopt(curl, CURLOPT_URL, url.c_str());
        curl_easy_setopt(curl, CURLOPT_POSTFIELDS, jsonData.c_str());
        curl_easy_setopt(curl, CURLOPT_POSTFIELDSIZE, jsonData.length());

        responseBuffer.clear();
        CURLcode res = curl_easy_perform(curl);
        if (res != CURLE_OK) {
            throw std::runtime_error(curl_easy_strerror(res));
        }

        return parseJsonResponse(responseBuffer);
    }
};

int main() {
    try {
        ApiClient apiClient;

        // Example usage of executeQuery
        Json::Value queryResult = apiClient.executeQuery("SELECT * FROM PEOPLE");
        std::cout << "Query Result: " << queryResult.toStyledString() << std::endl;
        
        // Example usage of listTables
        Json::Value tables = apiClient.listTables();
        std::cout << "Tables: " << tables.toStyledString() << std::endl;

        // Example usage of insert
        Json::Value insertResult = apiClient.insert("PEOPLE", "[{\"name\":\"John Doe\",\"age\":30}]");
        std::cout << "Insert Result: " << insertResult.toStyledString() << std::endl;

        // Example usage of deleteData
        Json::Value deleteResult = apiClient.deleteData("PEOPLE", "[\"name\"]", "[\"John Doe\"]");
        std::cout << "Delete Result: " << deleteResult.toStyledString() << std::endl;

        // Example usage of select
        Json::Value selectResult = apiClient.select("PEOPLE", "[\"name\", \"age\"]", "age > ?", "[25]");
        std::cout << "Select Result: " << selectResult.toStyledString() << std::endl;
        
    } catch (const std::exception& e) {
        std::cerr << "Error: " << e.what() << std::endl;
    }

    return 0;
}
