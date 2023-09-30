package com.example.demo.entities;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String logradouro;
    private Long cep;
    private Long numero;

    private String cidade;
    private boolean isPrincipal;

    public Address(String logradouro, Long cep, Long numero, String cidade, boolean isPrincipal) {
        this.logradouro = logradouro;
        this.cep = cep;
        this.numero = numero;
        this.cidade = cidade;
        this.isPrincipal = isPrincipal;
    }

    public Address() {
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public Long getCep() {
        return cep;
    }

    public void setCep(Long cep) {
        this.cep = cep;
    }

    public Long getNumero() {
        return numero;
    }

    public void setNumero(Long numero) {
        this.numero = numero;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public boolean getIsPrincipal() {
        return isPrincipal;
    }

    public void setPrincipal(boolean principal) {
        isPrincipal = principal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Address address)) return false;
        return isPrincipal == address.isPrincipal && Objects.equals(getLogradouro(), address.getLogradouro()) && Objects.equals(getCep(), address.getCep()) && Objects.equals(getNumero(), address.getNumero()) && Objects.equals(getCidade(), address.getCidade());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLogradouro(), getCep(), getNumero(), getCidade(), isPrincipal);
    }
}
