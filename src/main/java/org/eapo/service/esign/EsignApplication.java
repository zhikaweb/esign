package org.eapo.service.esign;


import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.eapo.service.esign.ws.Convert2PDFClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.config.annotation.EnableWs;
import org.springframework.ws.config.annotation.WsConfigurerAdapter;
import org.springframework.ws.transport.http.MessageDispatcherServlet;
import org.springframework.ws.wsdl.wsdl11.DefaultWsdl11Definition;
import org.springframework.xml.xsd.SimpleXsdSchema;
import org.springframework.xml.xsd.XsdSchema;

import java.security.Security;

@EnableWs
@SpringBootApplication
@Configuration
@ComponentScan(basePackages = {"org.eapo.service.esign.rest",
        "org.eapo.service.esign.service",
        "org.eapo.service.esign.service.converter",
        "org.eapo.service.esign.service.phoenix",
        "org.eapo.service.esign.service.stamper",
        "org.eapo.service.esign.crypto",
        "org.eapo.service.esign.exception",
        "org.eapo.service.esign.ws"
})
public class EsignApplication extends WsConfigurerAdapter {

    @Value("${esigner.endpoints.docx2pdf:http://localhost:8090/ws}")
    private String docx2pdfEndpoint;

    private static Logger logger = LoggerFactory.getLogger(EsignApplication.class.getName());

    public static void main(String[] args) throws Exception {
        logger.info("Starting Esign service...");
        addBouncyCastleAsSecurityProvider();
        ApplicationContext context = SpringApplication.run(EsignApplication.class, args);
        logger.info("Esign service is ready");
    }

    public static void addBouncyCastleAsSecurityProvider() {
        logger.debug("Adding BouncyCastleAsSecurityProvider");
        Security.setProperty("crypto.policy", "unlimited");
        Security.addProvider(new BouncyCastleProvider());
        logger.debug("BouncyCastle is ready!");
    }


    @Bean
    public Jaxb2Marshaller marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("org.eapo.pdfconverter.service");
        return marshaller;
    }
    @Bean
    public Convert2PDFClient countryClient(Jaxb2Marshaller marshaller) {

        System.out.println(docx2pdfEndpoint);

        Convert2PDFClient client = new Convert2PDFClient();
        client.setDefaultUri(docx2pdfEndpoint);  //"http://192.168.2.150:8090/ws/"
        client.setMarshaller(marshaller);
        client.setUnmarshaller(marshaller);
        return client;
    }


    @Bean
    public ServletRegistrationBean messageDispatcherServlet(ApplicationContext applicationContext) {
        MessageDispatcherServlet servlet = new MessageDispatcherServlet();
        servlet.setApplicationContext(applicationContext);
        servlet.setTransformWsdlLocations(true);
        return new ServletRegistrationBean(servlet, "/ws/*");
    }

    @Bean(name = "phoenix-upload")
    public DefaultWsdl11Definition defaultWsdl11Definition(XsdSchema phoenixUploadSchema) {
        DefaultWsdl11Definition wsdl11Definition = new DefaultWsdl11Definition();
        wsdl11Definition.setPortTypeName("UploadPort");
        wsdl11Definition.setLocationUri("/ws");
        wsdl11Definition.setTargetNamespace("http://service.upload.phoenix.eapo.org");
        wsdl11Definition.setSchema(phoenixUploadSchema);
        return wsdl11Definition;
    }

    @Bean
    public XsdSchema uploadSchema() {
        return new SimpleXsdSchema(new ClassPathResource("phoenix-upload.xsd"));
    }
}
