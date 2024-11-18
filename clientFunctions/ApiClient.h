#ifndef API_CLIENT_H
#define API_CLIENT_H

#include <string>
#include <json/json.h>

class ApiClient {
private:
    CURL* curl = nullptr;
    struct curl_slist* headers = nullptr;
    std::string responseBuffer;

    static size_t writeCallback(void* contents, size_t size, size_t nmemb, void* userp);
    Json::Value parseJsonResponse(const std::string& response);

public:
    ApiClient();
    ~ApiClient();

    Json::Value execQuery(const std::string& query);
    bool createTable(const std::string& tableSql);
    Json::Value listTables();
    bool insert(const std::string& tableName, const std::string& values);
    bool deleteData(const std::string& tableName, const std::string& columns, const std::string& values);
    Json::Value select(const std::string& tableName, const std::string& columns, const std::string& whereClause, const std::string& params);
};

#endif // API_CLIENT_H
