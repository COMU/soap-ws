package com.intellica.callws;

import java.util.List;

import javax.xml.namespace.QName;

import net.sf.json.JSONObject;

import org.apache.commons.cli.*;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.reficio.ws.SoapBuilderException;
import org.reficio.ws.SoapContext;
import org.reficio.ws.builder.SoapBuilder;
import org.reficio.ws.builder.SoapOperation;
import org.reficio.ws.builder.core.Wsdl;
import org.reficio.ws.client.core.SoapClient;
import org.reficio.ws.common.Parameter;
import org.reficio.ws.legacy.SoapMessage;

public class CallWS {

	public static void main(String[] args) {
		Options options = new Options();
		options.addOption("w", "wsdl", true, "soap wsdl url.");
		options.addOption("e", "endpoint", true, "soap endpoint url.");
		options.addOption("c", "check-endpoint", false, "check soap endpoint is accessible and well formed");
		options.addOption("l", "list-operations", false, "list operations in service");
		options.addOption("h", "help", false, "prints this message");
		options.addOption("b", "binding", true, "binding name");
		options.addOption("o", "operation", true, "operation name");
		options.addOption("lp", "list-parameters", false, "list input parameters of operation");
		options.addOption("p", "parameters", true, "input parameters for soap request (json)");
		

		CommandLineParser parser = new GnuParser();

		try {
			CommandLine cmd = parser.parse(options, args);

			if (cmd.hasOption("help")) {
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("callws", "", options, "", true);
				return;
			}
			
			if (!cmd.hasOption("wsdl")) {
				System.out.println("No soap wsdl specified.");
				System.out.println("Try `callws --help` parameter for more information.");
				return;
			}
			String wsdl_url = cmd.getOptionValue("wsdl");
			
			if (cmd.hasOption("check-endpoint")) {
				checkEndpoint(wsdl_url);
			}

			if (cmd.hasOption("list-operations")) {
				listOperations(wsdl_url);
			}
			
			if (cmd.hasOption("list-parameters")) {
				String binding_name = cmd.getOptionValue("binding");
				String operation_name = cmd.getOptionValue("operation");
				
				listParameters(wsdl_url, binding_name, operation_name);
			}
			
			if (cmd.hasOption("parameters")) {
				String binding_name = cmd.getOptionValue("binding");
				String operation_name = cmd.getOptionValue("operation");
				String endpoint = cmd.getOptionValue("endpoint");
				String parameters = cmd.getOptionValue("parameters", "");
				
				sendRequest(wsdl_url, binding_name, operation_name, endpoint, parameters);
			}

		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	private static void checkEndpoint(String wsdl_url) {
		try {
			Wsdl.parse(wsdl_url);
			System.out.println("\"" + wsdl_url + "\" is accessible and well formed.");
		}
		catch (SoapBuilderException e) {
			System.out.println(e.getMessage());
		}
	}
	
	private static void listOperations(String wsdl_url) {
		try {
			Wsdl wsdl = Wsdl.parse(wsdl_url);
			List<QName> bindings = wsdl.getBindings();

			for (QName binding : bindings) {
				SoapBuilder builder = wsdl.getBuilder(binding);
				for (SoapOperation operation : builder.getOperations()) {
					System.out.println("Binding name: " + binding.getLocalPart() + ", Operation name: " + operation.getOperationName());
				}
			}
		}
		catch (SoapBuilderException e) {
			System.out.println(e.getMessage());
		}
	}
	
	private static void listParameters(String wsdl_url, String binding_name, String operation_name) {
		if (binding_name == null || operation_name == null) {
			System.out.println("Binding and operation name required for listing parameters.");
			System.out.println("Try `callws --help` parameter for more information.");
			return;
		}		
						
		Wsdl wsdl = Wsdl.parse(wsdl_url);

		SoapBuilder builder = wsdl.binding().localPart(binding_name).find();
		
		SoapOperation operation = builder.operation().name(operation_name).find();

		for (Parameter param : builder.getParameters(operation)) {
			System.out.println("Parameter name: " + param.getName() + ", Parameter type: " + param.getType());
		}
	}
	
	private static void sendRequest(String wsdl_url, String binding_name, String operation_name, String endpoint, String parameters) {
		if (binding_name == null || operation_name == null) {
			System.out.println("Binding and operation name required for sending soap request.");
			System.out.println("Try `callws --help` parameter for more information.");
			return;
		}
		JSONObject jsonObject = JSONObject.fromObject(parameters);
	
		Wsdl wsdl = Wsdl.parse(wsdl_url);
		SoapBuilder builder = wsdl.binding().localPart(binding_name).find();
		SoapOperation operation = builder.operation().name(operation_name).find();
	    SoapContext context = SoapContext.builder()
	    		.alwaysBuildHeaders(true)
	            .exampleContent(false)
	            .build();
	    
	    SoapMessage request = new SoapMessage(builder.buildInputMessage(operation, context));
	    
		for (Object param : jsonObject.names()) {
			String paramName = param.toString();
			String paramValue = jsonObject.getString(param.toString());
			
			request.setParam(paramName, paramValue);
	    }
	    
		String request_url;
		if (endpoint != null) {
			request_url = endpoint;
		} else {
			request_url = wsdl_url;
		}
		
		SoapClient client = SoapClient.builder().endpointUri(request_url).build();
		String response = client.post(operation.getSoapAction(), request.toString());
		
		/* pretty print the response */
		
		XmlObject xml_response;
		try {
			xml_response = XmlObject.Factory.parse(response);
			response = xml_response.xmlText(new XmlOptions().setSavePrettyPrint().setSavePrettyPrintIndent(2));
		} catch (XmlException e) {
			e.printStackTrace();
		}
		
		System.out.println(response);
	}
}


