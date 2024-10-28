#include <iostream>
#include <string>
#include <curl/curl.h>

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
        headers = curl_slist_append(headers, "Content-Type: text/plain");
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
    void executeQuery(const std::string& query) {
        if (!curl) {
            std::cerr << "CURL is not initialized." << std::endl;
            return;
        }

        // URL for the POST request
        std::string url = "http://localhost:8080/api/execQuery";
        CURLcode res;

        // Set the URL and POST data for this specific request
        curl_easy_setopt(curl, CURLOPT_URL, url.c_str());
        curl_easy_setopt(curl, CURLOPT_POSTFIELDS, query.c_str());
        curl_easy_setopt(curl, CURLOPT_POSTFIELDSIZE, query.length());

        // Clear the response buffer
        responseBuffer.clear();

        // Perform the request
        res = curl_easy_perform(curl);
        if (res != CURLE_OK) {
            std::cerr << "curl_easy_perform() failed: " << curl_easy_strerror(res) << std::endl;
        } else {
            // Print the response received from the API
            std::cout << "Response:\n" << responseBuffer << std::endl;
        }
    }

    // Function to perform a GET request to /listTables
    std::string listTables() {
        if (!curl) {
            std::cerr << "CURL is not initialized." << std::endl;
            return "";
        }

        // URL for the GET request
        std::string url = "http://localhost:8080/api/listTables";
        CURLcode res;

        // Set the URL for this specific request
        curl_easy_setopt(curl, CURLOPT_URL, url.c_str());
        curl_easy_setopt(curl, CURLOPT_HTTPGET, 1L);

        // Clear the response buffer
        responseBuffer.clear();

        // Perform the request
        res = curl_easy_perform(curl);
        if (res != CURLE_OK) {
            std::cerr << "curl_easy_perform() failed: " << curl_easy_strerror(res) << std::endl;
            return "";
        }

        // Return the response received from the API
        return responseBuffer;
    }
};

int main() {
    try {
        ApiClient apiClient;
        std::string query = "SELECT * FROM PEOPLE";
        apiClient.executeQuery(query);

        // Example usage of listTables
        std::string tables = apiClient.listTables();
        std::cout << tables << std::endl;
    } catch (const std::exception& e) {
        std::cerr << "Error: " << e.what() << std::endl;
    }

    return 0;
}
