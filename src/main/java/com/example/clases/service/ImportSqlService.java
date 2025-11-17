package com.example.clases.service;

import java.util.List;

public interface ImportSqlService {
    void regenerateImportSql();
    void updateImportSqlAfterUserOperation();
    List<String> generateUserInserts();
    void writeImportSqlFile(List<String> insertStatements);
}