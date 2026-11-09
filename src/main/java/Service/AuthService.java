package Service;

import Exceptions.RegleGestionException;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.nio.file.Path;

public class AuthService {
    private String path;
    private JsonMapper mapper = JsonMapper.builder()
            .enable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .build();
    private JsonNode root = mapper.readTree(Path.of("user.json").toFile());
    private int tentativeRestante;

    public AuthService(String path) throws IOException {
        this.path = path;
        this.tentativeRestante = 3;
    }

    public boolean login(String utilisateur, String motDePasse){
        if(tentativeRestante>0){
            String motDePasseBase = "";
            if(root.get("login") != null && root.get("login").asText().equals(utilisateur)) {
                motDePasseBase = root.get("motDePasseHash") != null ? root.get("motDePasseHash").asText() : "";
            }

            if(motDePasse.isEmpty() || motDePasseBase.isEmpty()) {
                tentativeRestante--;
                return false;
            }

            BCrypt.Result result = BCrypt.verifyer().verify(motDePasse.toCharArray(), motDePasseBase);

            if(result.verified) return true;
            else{
                tentativeRestante--;
                return false;
            }
        } else {
            throw new RegleGestionException("Identifiants incorrects. Nombre maximal de tentatives atteint. \nFermeture de l'application.");
        }

    }

    public int getTentativeRestante(){
        return this.tentativeRestante;
    }

}
