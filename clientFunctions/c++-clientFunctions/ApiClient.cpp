#include "ApiClient.h"
#include <curl/curl.h>
#include <iostream>

ApiClient::ApiClient(const std::string& baseUrl) : baseUrl(baseUrl) {}

ApiClient::~ApiClient() {}

// Callback function for libcurl to write response data into a string
static size_t WriteCallback(void* contents, size_t size, size_t nmemb, std::string* userData) {
    size_t totalSize = size * nmemb;
    userData->append(static_cast<char*>(contents), totalSize);
    return totalSize;
}

std::string ApiClient::sendRequest(const std::string& url, const std::string& method, const std::string& body) {
    CURL* curl = curl_easy_init();
    if (!curl) {
        std::cerr << "Failed to initialize libcurl" << std::endl;
        return "";
    }

    std::string responseData;
    struct curl_slist* headers = nullptr;

    curl_easy_setopt(curl, CURLOPT_URL, url.c_str());
    curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, WriteCallback);
    curl_easy_setopt(curl, CURLOPT_WRITEDATA, &responseData);

    // Set headers
    headers = curl_slist_append(headers, "Content-Type: application/json");
    curl_easy_setopt(curl, CURLOPT_HTTPHEADER, headers);

    if (method == "POST") {
        curl_easy_setopt(curl, CURLOPT_POST, 1L);
        curl_easy_setopt(curl, CURLOPT_POSTFIELDS, body.c_str());
    }

    CURLcode res = curl_easy_perform(curl);
    if (res != CURLE_OK) {
        std::cerr << "libcurl error: " << curl_easy_strerror(res) << std::endl;
    }

    curl_easy_cleanup(curl);
    curl_slist_free_all(headers);

    return responseData;
}

nlohmann::json ApiClient::execQuery(const std::string& query) {
    std::string jsonData = "{\"query\": \"" + query + "\"}";
    std::string response = sendRequest(baseUrl + "/api/execQuery", "POST", jsonData);
    return response.empty() ? nullptr : nlohmann::json::parse(response);
}

bool ApiClient::createTable(const std::string& tableSql) {
    std::string response = sendRequest(baseUrl + "/api/createTable", "POST", tableSql);
    if (response.empty()) return false;
    auto jsonResponse = nlohmann::json::parse(response);
    return jsonResponse["status"] == "success";
}

nlohmann::json ApiClient::listTables() {
    std::string response = sendRequest(baseUrl + "/api/listTables", "GET");
    return response.empty() ? nullptr : nlohmann::json::parse(response);
}

bool ApiClient::insert(const std::string& tableName, const std::string& values) {
    try {
        nlohmann::json::parse(values); // Validate JSON
        std::string jsonData = "{\"tableName\": \"" + tableName + "\", \"values\": " + values + "}";
        std::string response = sendRequest(baseUrl + "/api/insert", "POST", jsonData);
        if (response.empty()) return false;
        auto jsonResponse = nlohmann::json::parse(response);
        return jsonResponse["status"] == "success";
    } catch (const std::exception& e) {
        std::cerr << "Error during insert: " << e.what() << std::endl;
        return false;
    }
}

bool ApiClient::deleteEntry(const std::string& tableName, const std::string& columns, const std::string& values) {
    std::string jsonData = "{\"tableName\": \"" + tableName + "\", \"columns\": " + columns + ", \"values\": " + values + "}";
    std::string response = sendRequest(baseUrl + "/api/delete", "POST", jsonData);
    if (response.empty()) return false;
    auto jsonResponse = nlohmann::json::parse(response);
    return jsonResponse["status"] == "success";
}

nlohmann::json ApiClient::select(const std::string& tableName, const std::string& columns, const std::string& whereClause, const std::string& params) {
    std::string jsonData = "{\"tableName\": \"" + tableName + "\", \"columns\": " + columns + ", \"whereClause\": \"" + whereClause + "\", \"params\": " + params + "}";
    std::string response = sendRequest(baseUrl + "/api/select", "POST", jsonData);
    return response.empty() ? nullptr : nlohmann::json::parse(response);
}

