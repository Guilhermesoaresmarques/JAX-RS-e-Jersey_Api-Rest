package br.com.alura.loja.dao;

import java.net.URI;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import br.com.alura.loja.modelo.Produto;
import br.com.alura.loja.servidor.Servidor;
import javafx.animation.PathTransitionBuilder;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.filter.LoggingFilter;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import br.com.alura.loja.modelo.Carrinho;

import com.thoughtworks.xstream.XStream;

public class ClienteTest {

    private HttpServer server;
    private WebTarget target;
    private Client client;

    @Before
    public void startaServidor() {
        server = Servidor.inicializaServidor();
        ClientConfig config = new ClientConfig();
        config.register(new LoggingFilter());
        this.client = ClientBuilder.newClient(config);
        this.target = client.target("http://localhost:8080");
    }

    @After
    public void mataServidor() {
        server.stop();
    }

    @Test
    public void testaQueBuscarUmCarrinhoTrazOCarrinhoEsperado() {
        Carrinho carrinho = target.path("/carrinhos/1").request().get(Carrinho.class);
        Assert.assertEquals("Rua Vergueiro 3185, 8 andar", carrinho.getRua());
    }

    @Test
    public void testaQueSuportaNovosCarrinhos() {
        Carrinho carrinho = new Carrinho();
        carrinho.adiciona(new Produto(314, "Microfone", 37, 1));
        carrinho.setRua("Rua Verguiro 3185");
        carrinho.setCidade("Sao Paulo");
        Entity<Carrinho> entity = Entity.entity(carrinho, MediaType.APPLICATION_XML);

        Response response = target.path("/carrinhos").request().post(entity);
        Assert.assertEquals(201, response.getStatus());
        String location = response.getHeaderString("Location");

        Carrinho carrinhoCarregado = client.target(location).request().get(Carrinho.class);
        Assert.assertEquals("Microfone", carrinho.getProdutos().get(0).getNome());

    }
}