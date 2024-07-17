
package integration;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.MethodSource;

import ast.AbstractSyntaxTree;
import ast.AbstractSyntaxTreeFactory;
import emitter.Emitter;
import error.CompileError;
import integration.IntegrationTest.AsmRunner.Result;
import lexer.Lexer;
import parser.ParseTree;
import parser.Parser;

/**
 * integrationTest
 */
public class IntegrationTest {
  /**
   * AsmRunner
   */
  public class AsmRunner {
    public record Result(int exitCode, String stdOut, String errOut) {
    }

    /**
     * Result
     */
    Process p;
    BufferedReader stdInputReader;
    PrintWriter stdOutputWriter;

    BufferedReader errOutReader;
    String stdOut = "";
    String errOut = "";

    int exitCode = 0;

    public AsmRunner(String file) throws IOException {
      this.p = Runtime.getRuntime().exec(getCommand(file), null, null);
      stdInputReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
      errOutReader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
      stdOutputWriter = new PrintWriter(p.getOutputStream());
    }

    public AsmRunner writeLine(String s) throws IOException {
      stdOutputWriter.println(s);
      stdOutputWriter.flush();
      return this;
    }

    public AsmRunner readLine() throws IOException {
      String r = null;
      if ((r = stdInputReader.readLine()) != null) {
        stdOut += r;
      }
      return this;
    }

    public AsmRunner readAll() throws IOException {
      String r = null;
      while ((r = stdInputReader.readLine()) != null) {
        stdOut += r;
      }
      return this;
    }

    public AsmRunner readErr() throws IOException {
      String r = null;
      if ((r = errOutReader.readLine()) != null) {
        errOut += r;
      }
      return this;
    }

    public AsmRunner readAllErr() throws IOException {
      String r = null;
      while ((r = errOutReader.readLine()) != null) {
        errOut += r;
      }
      return this;
    }

    public Result waitFor() throws InterruptedException, IOException {
      closeAll();
      exitCode = p.waitFor();
      return new Result(exitCode, stdOut, errOut);

    }

    public Result waitFor(int ms) throws InterruptedException, IOException {
      closeAll();
      if (!p.waitFor(ms, TimeUnit.MILLISECONDS)) {
        p.destroy();
        return new Result(0, stdOut, errOut);
      }
      exitCode = p.exitValue();
      return new Result(exitCode, stdOut, errOut);

    }

    private void closeAll() throws IOException {
      stdInputReader.close();
      stdOutputWriter.close();
      errOutReader.close();

    }

    private String[] getCommand(String file) {
      final Supplier<Stream<String>> COMMAND = () -> Arrays
          .asList(new String[] { "java", "-jar", "src/test/resources/Mars4_5.jar", "nc" }).stream();
      return Stream.concat(COMMAND.get(), Stream.of(file)).toArray(String[]::new);

    }

  }

  private static int isPrime(int n) {
    if (n <= 1)
      return 0;

    for (int i = 2; i < n; i++)
      if (n % i == 0)
        return 0;

    return 1;
  }

  private static Stream<Arguments> inputVariousCalculations() {
    return Stream.of(
        Arguments.of("""
            int i = (5 - 3) * -4 / (2 - 4);
            print(i);
            """, "4"),
        Arguments.of("""
            int i = (3+ - (43 + 4 *-13));
            print(i);
            """, "12"),
        Arguments.of("""
            int i = 100 % (6 * 34);
            print(i);
            """, "100"),
        Arguments.of("""
            int i = (6 * 34) / (3+ - (43 + 4 *-13));
            print(i);
            """, "17"),
        Arguments.of("""
            int i = 100 % (6 * 34) / (3+ - (43 + 4 *-13));
            print(i);
            """, "15"),
        Arguments.of("""
            int i = 100 % 17;
            print(i);
            """, "15"),
        Arguments.of("""
            int i = 123 * ((4 * 4) * -49 -34);
            print(i);
            """, "-100614"),
        Arguments.of("""
            int i = 100 / 12;
            print(i);
            """, "8"));
  }

  @ParameterizedTest
  @MethodSource("inputVariousCalculations")
  public void variousCalculations(String input, String expected)
      throws IOException, CompileError, InterruptedException {
    Lexer l = new Lexer(input);
    Parser p = new Parser(l.genTokens());
    ParseTree pt = new ParseTree();
    p.program(pt);
    AbstractSyntaxTreeFactory astf = new AbstractSyntaxTreeFactory();

    AbstractSyntaxTree ast = astf.fromParseTree(pt);
    Emitter emitter = new Emitter(ast, astf.symbolTable);
    emitter.generate();
    String f = "src/test/resources/asm/integration/various.asm";
    file.Writer.write(f, emitter.getCode().toString());
    Result r = new AsmRunner(f).readAll().readAllErr().waitFor();

    assertEquals("", r.errOut());
    assertEquals(expected, r.stdOut(), input);
    assertEquals(0, r.exitCode());

  }

  @ParameterizedTest
  @CsvFileSource(resources = "/fibonacci_sequence.csv", numLinesToSkip = 1)
  public void fibonacci(String input, String expected)
      throws IOException, CompileError, InterruptedException {
    String code = """
        int n = readI();;;;;;
        if(n<=1) {
          print(n);
          exit();
        }
        int fib = 1;
        int prevFib = 1;
        int i = 2;
        while(i<n){
          int tmp = fib;
          fib = fib + prevFib;
          prevFib = tmp;
          i = i + 1;
        }
        print(fib);
        """;
    Lexer l = new Lexer(code);
    Parser p = new Parser(l.genTokens());
    ParseTree pt = new ParseTree();
    p.program(pt);
    AbstractSyntaxTreeFactory astf = new AbstractSyntaxTreeFactory();

    AbstractSyntaxTree ast = astf.fromParseTree(pt);
    Emitter emitter = new Emitter(ast, astf.symbolTable);
    emitter.generate();
    String f = "src/test/resources/asm/integration/fibinacci.asm";
    file.Writer.write(f, emitter.getCode().toString());
    Result r = new AsmRunner(f).writeLine(input).readLine().readAllErr().waitFor();

    assertEquals("", r.errOut());
    assertEquals(expected, r.stdOut(), code + "\n" + ast);
    assertEquals(0, r.exitCode());

  }

  @RepeatedTest(20)
  public void primeTest()
      throws IOException, CompileError, InterruptedException {
    int randomNum = ThreadLocalRandom.current().nextInt(0, 1000 + 1);
    String code = """
        int n = readI();
        if(n <= 1) {
          print(false);
          exit();
        }
        int i = 2;
        while(i<n){
          if(n % i == 0){
            print(false);
            exit();
          }
          i = i +1;
        }
        print(true);
        """;
    Lexer l = new Lexer(code);
    Parser p = new Parser(l.genTokens());
    ParseTree pt = new ParseTree();
    p.program(pt);
    AbstractSyntaxTreeFactory astf = new AbstractSyntaxTreeFactory();

    AbstractSyntaxTree ast = astf.fromParseTree(pt);
    Emitter emitter = new Emitter(ast, astf.symbolTable);
    emitter.generate();
    String dir = "src/test/resources/";
    String f = "src/test/resources/asm/integration/isPrime.asm";
    file.Writer.write(f, emitter.getCode().toString());
    file.Writer.write(dir+"isPrimeParseTree.txt", pt.toString());
    file.Writer.write(dir+"isPrimeAST.txt", ast.toString());
    Result r = new AsmRunner(f).writeLine("" + randomNum).readLine().readAllErr().waitFor();

    assertEquals("", r.errOut());
    assertEquals(String.format("%d", isPrime(randomNum)), r.stdOut(), code + "\n" + ast);
    assertEquals(0, r.exitCode());

  }

}
