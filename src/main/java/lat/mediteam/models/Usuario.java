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
import lombok.Getter;
import lombok.Setter;

@Entity
public class Usuario {
    
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Getter
    @Setter
    @Column(nullable = false, unique = true)
    private String email;

    @Getter
    @Setter
    @Column(nullable = false)
    private String password;

    @Getter
    @Setter
    @Enumerated(EnumType.STRING)
    private UsuarioTipo tipo;

    @Getter
    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private Admin admin;

    @Getter
    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private Medico medico;

    @Getter
    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private Paciente paciente;

    @Getter
    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private Secretaria secretaria;
    
    public Usuario() {
        // Constructor por defecto requerido por JPA
    }
    public Usuario(String email, String password, UsuarioTipo tipo) {
        this.email = email;
        this.password = password;
        this.tipo = tipo;
    }
}
