package br.uece.eesdevops.rodneybarreto.util;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class JpaUtil {

    private final EntityManagerFactory entityManagerFactory;

    public JpaUtil() {
        try {
            entityManagerFactory = Persistence.createEntityManagerFactory("LivrariaDS");
        } catch (Exception e) {
            throw new RuntimeException("Não foi possível efetuar conexão com o banco de dados: " + e.getMessage());
        }
    }

    public EntityManagerFactory getEntityManagerFactory() {
        return entityManagerFactory;
    }

}
