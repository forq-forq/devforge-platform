package com.devforge.platform.practice.service;

import com.devforge.platform.practice.domain.Problem;
import com.devforge.platform.practice.domain.TestCase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.tools.*;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.net.URI;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.*;

/**
 * Service responsible for compiling and running student code.
 * NOTE: Currently runs code in-memory.
 */
@Service
@Slf4j
public class CodeExecutionService {

    /**
     * Compiles the user's code and runs it against all defined test cases.
     *
     * @param userCode The source code submitted by the student.
     * @param problem  The problem entity containing test cases and method config.
     * @return true if all tests pass, false otherwise.
     */
    public boolean execute(String userCode, Problem problem) {
    log.info("Compiling user code for problem: {}", problem.getMethodName());

    ExecutorService executor = Executors.newSingleThreadExecutor();

    try {
        Class<?> compiledClass = compile(problem.getClassName(), userCode);
        Object instance = compiledClass.getDeclaredConstructor().newInstance();
        Class<?>[] paramTypes = parseSignature(problem.getMethodSignature());
        Method method = compiledClass.getMethod(problem.getMethodName(), paramTypes);

        for (TestCase test : problem.getTestCases()) {
            Object[] args = parseInput(test.getInputData(), paramTypes);

            // Run with timeout
            Future<Object> future = executor.submit(() -> method.invoke(instance, args));
            
            Object result;
            try {
                result = future.get(2, TimeUnit.SECONDS); // Wait max 2 sec
            } catch (TimeoutException e) {
                future.cancel(true);
                log.warn("Time Limit Exceeded for input: {}", test.getInputData());
                return false; 
            }

            String actual = String.valueOf(result);
            if (!actual.equals(test.getExpectedOutput())) {
                return false;
            }
        }
        return true;

    } catch (Exception e) {
        log.error("Error", e);
        return false;
    } finally {
        executor.shutdownNow();
    }
}

    /**
     * Uses the standard Java Compiler API to compile source code from a String
     * and load the resulting Bytecode into a Class object.
     * 
     * @param className  Name of the class (e.g. "Solution").
     * @param sourceCode Content of the java file.
     * @return The loaded Class<?> object.
     * @throws Exception if compilation fails.
     */
    private Class<?> compile(String className, String sourceCode) throws Exception {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
        
        // Map to hold the bytecode in memory instead of writing to disk
        Map<String, ByteArrayOutputStream> byteCodes = new HashMap<>();

        JavaFileManager inMemoryFileManager = new ForwardingJavaFileManager<>(fileManager) {
            @Override
            public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind, FileObject sibling) {
                return new SimpleJavaFileObject(URI.create("string:///" + className.replace('.', '/') + kind.extension), kind) {
                    @Override
                    public OutputStream openOutputStream() {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        byteCodes.put(className, baos);
                        return baos;
                    }
                };
            }
        };

        // Wrap source code in a file object
        JavaFileObject source = new SimpleJavaFileObject(URI.create("string:///" + className.replace('.', '/') + JavaFileObject.Kind.SOURCE.extension), JavaFileObject.Kind.SOURCE) {
            @Override
            public CharSequence getCharContent(boolean ignoreEncodingErrors) {
                return sourceCode;
            }
        };

        // Run compilation task
        boolean success = compiler.getTask(null, inMemoryFileManager, null, null, null, List.of(source)).call();
        if (!success) {
            throw new RuntimeException("Compilation failed. Check syntax.");
        }

        // Custom ClassLoader to load bytes from our map
        ClassLoader classLoader = new ClassLoader() {
            @Override
            protected Class<?> findClass(String name) throws ClassNotFoundException {
                ByteArrayOutputStream baos = byteCodes.get(name);
                if (baos == null) return super.findClass(name);
                byte[] bytes = baos.toByteArray();
                return defineClass(name, bytes, 0, bytes.length);
            }
        };

        return classLoader.loadClass(className);
    }

    /**
     * Helper to convert string signatures into Class types.
     * Only supports 'int' and 'String' for MVP.
     */
    private Class<?>[] parseSignature(String signature) {
        if (signature == null || signature.isBlank()) return new Class<?>[0];
        
        String[] parts = signature.split(",");
        Class<?>[] types = new Class<?>[parts.length];
        
        for (int i = 0; i < parts.length; i++) {
            String typeName = parts[i].trim();
            if (typeName.equals("int")) types[i] = int.class;
            else if (typeName.equals("String")) types[i] = String.class;
            // TODO: Add support for other primitives
        }
        return types;
    }

    /**
     * Helper to parse comma-separated input strings into Objects.
     */
    private Object[] parseInput(String inputData, Class<?>[] types) {
        if (inputData == null || inputData.isBlank()) return new Object[0];

        String[] parts = inputData.split(",");
        Object[] args = new Object[parts.length];
        
        for (int i = 0; i < parts.length; i++) {
            String val = parts[i].trim();
            if (types[i] == int.class) {
                args[i] = Integer.parseInt(val);
            } else {
                args[i] = val; // Default to String
            }
        }
        return args;
    }
}