#ifndef API_CLIENT_H
#define API_CLIENT_H

#include <string>
#include "json.hpp"


class ApiClient {
public:
    explicit ApiClient(const std::string& baseUrl);
    ~ApiClient();

    nlohmann::json execQuery(const std::string& query);
    bool createTable(const std::string& tableSql);
    nlohmann::json listTables();
    bool insert(const std::string& tableName, const std::string& values);
    bool deleteData(const std::string& tableName, const std::string& columns, const std::string& values);
    nlohmann::json select(const std::string& tableName, const std::string& columns, const std::string& whereClause, const std::string& params);

private:
    std::string baseUrl;

    std::string sendRequest(const std::string& url, const std::string& method, const std::string& body = "");
};

#endif // API_CLIENT_H

