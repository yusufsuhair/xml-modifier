import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import util.Constant;

public class Main {
  public static void main(String[] args) throws Exception {

    Supplier<Stream<Path>> supplier = () -> {
      try {
        return Files.list(Paths.get(Constant.FILES_DIRECTORY));
      } catch (IOException e) {
        e.printStackTrace();
      }
      return null;
    };

    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    DocumentBuilder builder = factory.newDocumentBuilder();

    for (int k = 1; k <= Objects.requireNonNull(supplier.get()).count(); k++) {

      String directory = String.format(Constant.XML_DIR, k);
      int dublin_core_size = countUsingDOM(directory, Constant.XML_ELEMENT);
      Document document = builder.parse(new File(String.format(directory, k)));

      Node root = document.getFirstChild();

      for (int i = 0; i < dublin_core_size - 1; i++) {

        String contributor = document.getElementsByTagName(Constant.XML_ELEMENT).item(i)
            .getAttributes().getNamedItem(Constant.XML_NAMED_ITEM).getTextContent();

        if (contributor.equals("contributor")) {

          Node item = document.getElementsByTagName(Constant.XML_ELEMENT).item(i);
          String value = item.getTextContent();
          List<String> convertedRankList = Stream.of(value.split(";"))
              .collect(Collectors.toList());
          root.removeChild(item);

          System.out.println("Removed " + item.getTextContent() + " at " + directory);

          for (int j = 0; j < convertedRankList.size(); j++) {

            // append a new node to staff
            Element age = document.createElement(Constant.XML_ELEMENT);
            age.setAttribute(Constant.XML_NAMED_ITEM, "contributor");
            age.setAttribute(Constant.XML_NAMED_ITEM_2, "author");
            age.setTextContent(convertedRankList.get(j));
            root.appendChild(age);

            // write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(new File(directory));
            transformer.transform(source, result);

            System.out.println("Appended " + age.getTextContent() + " at " + directory + "\n");

          }
        }
      }
    }

  }

  private static int countUsingDOM(String xml, String nodeName) throws Exception {
    int count = 0;
    DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder domBuilder = domFactory.newDocumentBuilder();
    Document document = domBuilder.parse(new File(xml));

    Node node = document.getDocumentElement();
    while (node != null) {
      if (node.getNodeType() == Node.ELEMENT_NODE) {
        String name = node.getNodeName();
        if (name.equals(nodeName)) {
          count++;
        }
      }
      if (node.getFirstChild() != null) {
        node = node.getFirstChild();
      } else {
        while (node != null && node.getNextSibling() == null) {
          node = node.getParentNode();
        }
        if (node != null) {
          node = node.getNextSibling();
        }
      }
    }
    return count;
  }
}

