package dk.magenta.dafosts.clientcertificates.controller;

import dk.magenta.dafosts.DafoTokenGenerator;
import dk.magenta.dafosts.clientcertificates.users.DafoCertificateUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import sun.security.x509.X509CertImpl;

import java.security.Principal;

@Controller
public class CertificateGetTokenController {

    @Autowired
    DafoTokenGenerator dafoTokenGenerator;

    @RequestMapping("/get_token_for_service")
    public ModelAndView get_token_for_service() throws Exception {

        ModelAndView getTokenView = new ModelAndView("get_token");

        return getTokenView;
    }

    @RequestMapping("/get_token")
    @ResponseBody
    @PreAuthorize("hasAuthority('ROLE_CERTIFICATE_USER')")
    public ResponseEntity<String> get_token(Principal principal) throws Exception {
        PreAuthenticatedAuthenticationToken preAuthToken = (PreAuthenticatedAuthenticationToken)principal;
        DafoCertificateUserDetails userDetails = (DafoCertificateUserDetails)preAuthToken.getPrincipal();

        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.TEXT_PLAIN);
        return new ResponseEntity<String>(
                dafoTokenGenerator.deflateAndEncode(dafoTokenGenerator.getTokenXml(userDetails)),
                httpHeaders,
                HttpStatus.OK
        );
    }
}