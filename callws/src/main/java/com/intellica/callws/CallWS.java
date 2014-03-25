package com.intellica.callws;

import java.util.List;

import javax.xml.namespace.QName;

import org.apache.commons.cli.*;
import org.reficio.ws.SoapBuilderException;
import org.reficio.ws.SoapContext;
import org.reficio.ws.builder.SoapBuilder;
import org.reficio.ws.builder.SoapOperation;
import org.reficio.ws.builder.core.Wsdl;

public class CallWS {

	public static void main(String[] args) {
		Options options = new Options();
		options.addOption("e", "endpoint", true, "soap endpoint url");
		options.addOption("c", "check-endpoint", false, "check soap endpoint is accessible and well formed");
		options.addOption("l", "list-operations", false, "list operations in service");
		options.addOption("h", "help", false, "print this message");
		options.addOption("b", "binding", true, "binding name");
		options.addOption("o", "operation", true, "operation name");
		options.addOption("lp", "list-parameters", false, "list input parameters of operation.");
		

		CommandLineParser parser = new GnuParser();

		try {
			CommandLine cmd = parser.parse(options, args);

			if (cmd.hasOption("help")) {
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("callws", "", options, "", true);
				return;
			}
			
			if (!cmd.hasOption("endpoint")) {
				System.out.println("No soap endpoint specified.");
				System.out.println("Try `callws --help` parameter for more information.");
				return;
			}
			String endpoint = cmd.getOptionValue("endpoint");
			
			if (cmd.hasOption("check-endpoint")) {
				try {
					Wsdl.parse(endpoint);
					System.out.println("\"" + endpoint + "\" is accessible and well formed.");
				}
				catch (SoapBuilderException e) {
					System.out.println(e.getMessage());
				}
			}

			if (cmd.hasOption("list-operations")) {
				try {
					Wsdl wsdl = Wsdl.parse(endpoint);
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
			
			if (cmd.hasOption("list-parameters")) {
				String binding_name = cmd.getOptionValue("binding");
				String operation_name = cmd.getOptionValue("operation");
				
				if (binding_name == null || operation_name == null) {
					System.out.println("Binding and operation name required for listing parameters.");
					System.out.println("Try `callws --help` parameter for more information.");
					return;
				}		
								
				Wsdl wsdl = Wsdl.parse(endpoint);

				SoapBuilder builder = wsdl.binding().localPart(binding_name).find();
				
				SoapOperation operation = builder.operation().name(operation_name).find();

				for (String parameter : builder.getParameters(operation)) {
					System.out.println(parameter);
				}
			}

		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
}
