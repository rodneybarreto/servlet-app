package br.uece.eesdevops.rodneybarreto.servlet;

import br.uece.eesdevops.rodneybarreto.model.Livro;
import br.uece.eesdevops.rodneybarreto.util.JpaUtil;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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

        String forward = "";
        String action = req.getParameter("action");
        String livroId = req.getParameter("livroId");

        if ("remove".equals(action) && (livroId != null && !livroId.equals(""))) {
            Long id = Long.parseLong(livroId);
            this.delete(id);

            req.setAttribute("livros", this.lista());
            forward = "/livro.jsp";
        }
        else if ("edita".equals(action) && (livroId != null && !livroId.equals(""))) {
            Long id = Long.parseLong(livroId);
            Livro livro = entityManager.find(Livro.class, id);

            req.setAttribute("livro", livro);
            forward = "/form.jsp";
        }
        else if ("inclui".equals(action)) {
            forward = "/form.jsp";
        }
        else {
            req.setAttribute("livros", this.lista());
            forward = "/livro.jsp";
        }

        RequestDispatcher rd = req.getRequestDispatcher(forward);
        rd.forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String livroId = req.getParameter("id");
        Livro livro = null;

        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();

            if (livroId != null && !"".equals(livroId)) {
                Long id = Long.parseLong(livroId);

                livro = entityManager.find(Livro.class, id);
                livro.setTitulo(req.getParameter("titulo"));
                livro.setAutor(req.getParameter("autor"));
                livro.setResumo(req.getParameter("resumo"));
                livro.setAnoLancamento(req.getParameter("anoLancamento"));
            }
            else {
                livro = new Livro();
                livro.setTitulo(req.getParameter("titulo"));
                livro.setAutor(req.getParameter("autor"));
                livro.setResumo(req.getParameter("resumo"));
                livro.setAnoLancamento(req.getParameter("anoLancamento"));
            }
            entityManager.persist(livro);
            entityManager.flush();

            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("Não foi possível salvar o livro: "+ e.getMessage());
        }

        req.setAttribute("livros", this.lista());
        RequestDispatcher rd = req.getRequestDispatcher("/livro.jsp");
        rd.forward(req, resp);
    }

    private List<Livro> lista() {
        return entityManager
                .createQuery("select l from Livro l", Livro.class)
                .getResultList();
    }

    private void delete(Long livroId) {
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();

            Livro livro = entityManager.find(Livro.class, livroId);
            entityManager.remove(livro);
            entityManager.flush();

            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw new RuntimeException("Não foi possível deletar o livro: "+ e.getMessage());
        }
    }

    @Override
    public void destroy() {
        if (entityManager != null && entityManager.isOpen()) {
            entityManager.close();
        }
    }

}
