package unex.cume.mdai.SendaLite.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.http.HttpMethod;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.security.config.Customizer;

@Configuration
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Reactivar CSRF con CookieCsrfTokenRepository (seguro y compatible con fetch que envía X-XSRF-TOKEN)
            .csrf(csrf -> csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
            .cors(Customizer.withDefaults())
            .authorizeHttpRequests(auth -> auth
                // recursos estáticos (permitir img/ para logo y avatares)
                .requestMatchers("/css/**", "/js/**", "/images/**", "/img/**", "/webjars/**", "/favicon.ico").permitAll()
                // páginas públicas y login/register (no incluir /usuarios/** aquí)
                .requestMatchers("/", "/rutas/**", "/login", "/register").permitAll()
                // Nota: no permitir acceso público a /api/usuarios; se protege más abajo con role ADMIN
                // vistas de usuarios sólo accesibles por ADMIN
                .requestMatchers("/usuarios/**").hasRole("ADMIN")
                // permitir GET público para endpoints de rutas (listar/ver)
                .requestMatchers(HttpMethod.GET, "/api/rutas/**").permitAll()
                // requerir autenticación para operaciones que modifican datos en /api/rutas/**
                .requestMatchers(HttpMethod.POST, "/api/rutas/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "/api/rutas/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/api/rutas/**").authenticated()
                // endpoints de administración
                .requestMatchers("/admin/**", "/api/usuarios/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .permitAll()
                // Forzar siempre la redirección al inicio tras login para evitar usar SavedRequest que a veces guarda URLs no deseadas
                .defaultSuccessUrl("/", true)
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .permitAll()
            );
        return http.build();
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        .allowedOrigins("http://localhost:8080")
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowCredentials(true);
            }
        };
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        // NoOp encoder for local testing with plain-text passwords (NOT for production)
        return NoOpPasswordEncoder.getInstance();
    }
}
