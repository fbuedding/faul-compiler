package main;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.cli.*;

import ast.AbstractSyntaxTree;
import ast.AbstractSyntaxTreeFactory;
import emitter.Emitter;
import error.CompileError;
import file.Reader;
import file.Writer;
import lexer.Lexer;
import parser.ParseTree;
import parser.Parser;

public class Main {
  public static void main(String[] args) throws IOException {
    Options opts = new Options();
    opts.addOption("h", "help", false, "print this message");
    opts.addOption("o", "out", true, "Output file");
    opts.addOption("d", "debug", false, "Create parse tree and abstract sytntax tree");
    CommandLineParser parser = new DefaultParser();
    try {
      // parse the command line arguments
      CommandLine line = parser.parse(opts, args);
      if (line.hasOption("h")) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("faul-compiler <file> [options]", opts);
        System.exit(0);
      } else {
        if (line.getArgs().length == 0) {
          System.err.println("No file to compile provided");
        } else {
          String file = line.getArgs()[0];
          String[] fileNameSplitted = file.split("\\.");
          String fileName = "out";
          if (line.hasOption("o")) {
            fileName = line.getOptionValue("o");
          } else {
            if (fileNameSplitted.length == 0) {
              fileName = file;
            } else {
              fileName = fileNameSplitted[0];
            }
          }
          System.out.println("Compiling....");
          String code = Reader.readFile(file);
          Lexer l = new Lexer(code);
          try {
            Parser p = new Parser(l.genTokens());
            ParseTree pt = new ParseTree();
            p.program(pt);
            AbstractSyntaxTreeFactory astf = new AbstractSyntaxTreeFactory();

            AbstractSyntaxTree ast = astf.fromParseTree(pt);
            Emitter emitter = new Emitter(ast, astf.symbolTable);
            emitter.generate();

            String f = fileName + ".asm";
            Writer.write(f, emitter.getCode().toString());
            if (line.hasOption("d")) {
              System.out.println("Writing parse and abstract syntax tree...");
              Writer.write(fileName + "_parse_tree.txt", pt.toString());
              Writer.write(fileName + "_abstract_syntax_tree.txt", ast.toString());
              System.out.println("Parse and abstract syntax tree written");
            }
            System.out.println("Compiled to " + f);
            System.exit(0);
          } catch (CompileError e) {
            System.err.println(e.getMessage());
            System.err.println();

            System.err.println("---------------------------");
            String lines[] = code.split("\n");
            for (int i = Math.max(0, e.line - 2); i < Math.min(e.line + 1, lines.length); i++) {
              System.err.println(lines[i]);
              if (i == e.line - 1) {
                String s = "";
                for (int j = 0; j < e.linePos - 1; j++) {
                  s += " ";
                }
                s += "^";
                System.err.println(s);
              }
            }
            System.err.println("---------------------------");
            System.exit(1);
          }
        }
      }
    } catch (ParseException exp) {
      // oops, something went wrong
      System.err.println("Parsing failed.  Reason: " + exp.getMessage());
      System.exit(1);
    } catch (FileNotFoundException e) {
      System.err.println("File not found");
      System.exit(1);
    } finally {
      System.exit(0);
    }
  }

}
