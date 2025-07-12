package com.beltra.sma.testdatabase;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

//@Disabled
//@SpringBootTest
public class DatabaseTests {

    //@Autowired
//    private DataSource dataSource;

    /** Attenzione: questo metodo puo' generare problemi di tempistiche di connessione in
     *  fase di produzione. */

//    //@Test
//    void testDatabaseConnection() throws Exception {
//        try (Connection connection = dataSource.getConnection()) {
//            Statement stmt = connection.createStatement();
//
//            // Query per ottenere il nome del database
//            ResultSet rs = stmt.executeQuery("SELECT current_database()"); // Per PostgreSQL
//            if (rs.next()) {
//                System.out.println("##############################\nDatabase attualmente in uso: " + rs.getString(1) + "\n##############################\n");
//            }
//        }
//        finally {
//            dataSource.getConnection().close();
//        }
//    }



//    @Test
//    public void resettaAndPopolaUtentiAndRuoli() throws Exception {
//        try (Connection connection = dataSource.getConnection()) {
//            Statement stmt = connection.createStatement();
//
//            stmt.execute("CALL resetta_e_popola_utenti_ruoli()"); // esecuzione stored procedure su db visite_mediche
//
//            System.out.println("Stored Procedure eseguita correttamente.\n");
//
//        } finally {
//            dataSource.getConnection().close();
//        }
//    }





}
