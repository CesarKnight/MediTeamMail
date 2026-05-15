package lat.mediteam.models;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import lat.mediteam.enums.UsuarioTipo;

@Entity
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private UsuarioTipo tipo;

    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private Admin admin;

    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private Medico medico;

    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private Paciente paciente;
 
    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private Secretaria secretaria;
    
    public Usuario() {
    }
    
    public Usuario(String email, String password, UsuarioTipo tipo) {
        this.email = email;
        this.password = password;
        this.tipo = tipo;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public UsuarioTipo getTipo() {
        return tipo;
    }

    public Admin getAdmin() {
        return admin;
    }

    public void setAdmin(Admin admin) {
        if (this.tipo != UsuarioTipo.ADMIN) {
            throw new IllegalStateException("El tipo de usuario no es ADMIN");
        }
        this.admin = admin;
    }

    public Medico getMedico() {
        return medico;
    }

    public void setMedico(Medico medico) {
        if (this.tipo != UsuarioTipo.MEDICO) {
            throw new IllegalStateException("El tipo de usuario no es MEDICO");
        }
        this.medico = medico;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        if (this.tipo != UsuarioTipo.PACIENTE) {
            throw new IllegalStateException("El tipo de usuario no es PACIENTE");
        }
        this.paciente = paciente;
    }

    public Secretaria getSecretaria() {
        return secretaria;
    }

    public void setSecretaria(Secretaria secretaria) {
        if (this.tipo != UsuarioTipo.SECRETARIA) {
            throw new IllegalStateException("El tipo de usuario no es SECRETARIA");
        }
        this.secretaria = secretaria;
    }
}
