#include <iostream>
#include <string>
#include <sstream>
#include <curl/curl.h>
#include <json/json.h>

class ApiClient {
private:
    CURL* curl = nullptr;
    struct curl_slist* headers = nullptr;
    std::string responseBuffer;

    static size_t writeCallback(void* contents, size_t size, size_t nmemb, void* userp) {
        ((std::string*)userp)->append((char*)contents, size * nmemb);
        return size * nmemb;
    }

    Json::Value parseJsonResponse(const std::string& response) {
        Json::CharReaderBuilder reader;
        Json::Value jsonResponse;
        std::string errors;
        std::istringstream stream(response);
        if (!Json::parseFromStream(reader, stream, &jsonResponse, &errors)) {
            std::cerr << "Failed to parse JSON response: " << errors << std::endl;
            return Json::Value();
        }
        return jsonResponse;
    }

public:
    ApiClient() {
        curl = curl_easy_init();
        if (!curl) {
            throw std::runtime_error("Failed to initialize curl.");
        }
        curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, ApiClient::writeCallback);
        curl_easy_setopt(curl, CURLOPT_WRITEDATA, &responseBuffer);
        headers = curl_slist_append(headers, "Content-Type: application/json");
        curl_easy_setopt(curl, CURLOPT_HTTPHEADER, headers);
    }

    ~ApiClient() {
        if (curl) curl_easy_cleanup(curl);
        if (headers) curl_slist_free_all(headers);
    }

    Json::Value execQuery(const std::string& query) {
        std::string url = "http://localhost:8080/api/execQuery";
        curl_easy_setopt(curl, CURLOPT_URL, url.c_str());
        curl_easy_setopt(curl, CURLOPT_POSTFIELDS, query.c_str());
        curl_easy_setopt(curl, CURLOPT_POSTFIELDSIZE, query.length());

        responseBuffer.clear();
        CURLcode res = curl_easy_perform(curl);
        if (res != CURLE_OK) {
            std::cerr << "Curl error: " << curl_easy_strerror(res) << std::endl;
            return Json::Value();
        }
        return parseJsonResponse(responseBuffer);
    }

    Json::Value listTables() {
        std::string url = "http://localhost:8080/api/listTables";
        curl_easy_setopt(curl, CURLOPT_URL, url.c_str());
        curl_easy_setopt(curl, CURLOPT_HTTPGET, 1L);

        responseBuffer.clear();
        CURLcode res = curl_easy_perform(curl);
        if (res != CURLE_OK) {
            std::cerr << "Curl error: " << curl_easy_strerror(res) << std::endl;
            return Json::Value();
        }
        return parseJsonResponse(responseBuffer);
    }

    bool insert(const std::string& tableName, const std::string& values) {
        std::string jsonData = "{\"tableName\": \"" + tableName + "\", \"values\": " + values + "}";
        std::string url = "http://localhost:8080/api/insert";
        curl_easy_setopt(curl, CURLOPT_URL, url.c_str());
        curl_easy_setopt(curl, CURLOPT_POSTFIELDS, jsonData.c_str());
        curl_easy_setopt(curl, CURLOPT_POSTFIELDSIZE, jsonData.length());

        responseBuffer.clear();
        CURLcode res = curl_easy_perform(curl);
        if (res != CURLE_OK) {
            std::cerr << "Curl error: " << curl_easy_strerror(res) << std::endl;
            return false;
        }
        Json::Value response = parseJsonResponse(responseBuffer);
        return response["status"].asString() == "success";
    }

    bool deleteData(const std::string& tableName, const std::string& columns, const std::string& values) {
        std::string jsonData = "{\"tableName\": \"" + tableName + "\", \"columns\": " + columns + ", \"values\": " + values + "}";
        std::string url = "http://localhost:8080/api/delete";
        curl_easy_setopt(curl, CURLOPT_URL, url.c_str());
        curl_easy_setopt(curl, CURLOPT_POSTFIELDS, jsonData.c_str());
        curl_easy_setopt(curl, CURLOPT_POSTFIELDSIZE, jsonData.length());

        responseBuffer.clear();
        CURLcode res = curl_easy_perform(curl);
        if (res != CURLE_OK) {
            std::cerr << "Curl error: " << curl_easy_strerror(res) << std::endl;
            return false;
        }
        Json::Value response = parseJsonResponse(responseBuffer);
        return response["status"].asString() == "success";
    }

    Json::Value select(const std::string& tableName, const std::string& columns, const std::string& whereClause, const std::string& params) {
        std::string jsonData = "{\"tableName\": \"" + tableName + "\", \"columns\": " + columns + ", \"whereClause\": \"" + whereClause + "\", \"params\": " + params + "}";
        std::string url = "http://localhost:8080/api/select";
        curl_easy_setopt(curl, CURLOPT_URL, url.c_str());
        curl_easy_setopt(curl, CURLOPT_POSTFIELDS, jsonData.c_str());
        curl_easy_setopt(curl, CURLOPT_POSTFIELDSIZE, jsonData.length());

        responseBuffer.clear();
        CURLcode res = curl_easy_perform(curl);
        if (res != CURLE_OK) {
            std::cerr << "Curl error: " << curl_easy_strerror(res) << std::endl;
            return Json::Value();
        }
        return parseJsonResponse(responseBuffer);
    }
};

