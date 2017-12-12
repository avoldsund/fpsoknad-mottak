package no.nav.foreldrepenger.selvbetjening;

import static java.util.stream.Collectors.joining;

import javax.inject.Inject;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class IndexController {

   @Inject
   private final AktorIdKlient aktorClient;

   @Inject
   public  IndexController(AktorIdKlient aktorClient) {
      this.aktorClient = aktorClient;
   }

   @RequestMapping(method = {RequestMethod.GET}, value = "/")
   public ResponseEntity<String> index() {
      return new ResponseEntity<String>("Env vars: " + System.getenv().keySet().stream().collect(joining("<br>")), HttpStatus.OK);

   }


   @Override
   public String toString() {
      return getClass().getSimpleName()  +
         " [AktorIdKlient=" + aktorClient + "]";
   }

}
