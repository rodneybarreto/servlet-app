package br.uece.eesdevops.rodneybarreto.servlet;

import br.uece.eesdevops.rodneybarreto.model.Livro;
import br.uece.eesdevops.rodneybarreto.util.JpaUtil;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/livraria")
public class LivroServlet extends HttpServlet {

    private EntityManager entityManager;

    @Override
    public void init() {
        entityManager = new JpaUtil().getEntityManagerFactory().createEntityManager();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        List<Livro> livros = new ArrayList<>();
        livros = entityManager.createQuery("select l from Livro l", Livro.class).getResultList();

        req.setAttribute("livros", livros);

        RequestDispatcher rd = req.getRequestDispatcher("/livro.jsp");
        rd.forward(req, resp);
    }

    @Override
    public void destroy() {
        if (entityManager != null && entityManager.isOpen()) {
            entityManager.close();
        }
    }
}
