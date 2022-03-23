//import javax.xml.bind.JAXBContext;
//import java.io.File;
//
//public class xml_read {
//    public xml_read() {
//    }
//
//    private void read() {
//
//
//
//
//        try {
//            // create XML file
//            File file = new File("user2.xml");
//
//            // create an instance of `JAXBContext`
//            JAXBContext context = JAXBContext.newInstance(User.class);
//
//            // create an instance of `Marshaller`
//            Marshaller marshaller = context.createMarshaller();
//
//            // enable pretty-print XML output
//            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
//
//            // create user object
//            User user = new User(2, "Tom Deo", "tom.doe@example.com",
//                    new String[]{"Member", "Moderator"}, false);
//
//            // convert user object to XML file
//            marshaller.marshal(user, file);
//
//        } catch (JAXBException ex) {
//            ex.printStackTrace();
//        }
//    }
//}
//
//
//public class DynamicAttributes {
//    public static void main(final String[] args) {
//        try {
//            JAXBContext jc;
//            jc = JAXBContext.newInstance(XMLResponse.class);
//            final XMLResponse xmlResponse = new XMLResponse();
//            xmlResponse.getAttributes().put(new QName("version"), "1.1");
//            xmlResponse.getAttributes().put(new QName("version_old"), "1.0");
//            final Marshaller marshaller = jc.createMarshaller();
//            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
//            marshaller.marshal(xmlResponse, System.out);
//        } catch (final JAXBException e) {
//            e.printStackTrace();
//        }
//
//    }
//}