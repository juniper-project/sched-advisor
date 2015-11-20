/*
 * Copyright (c) 2015, Brno University of Technology, Faculty of Information Technology
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * Neither the name of sched-advisor nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package eu.juniper.sa.tool.utils;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * The class for searching for all classes in a given package and all its
 * sub-packages. Based on https://github.com/ddopson/java-class-enumerator
 *
 * @author ddopson
 * @author rychly
 */
public class ClassFinder {

    private static final char DOT = '.';
    private static final char SLASH = '/';
    private static final String CLASS_SUFFIX = ".class";
    private static final String JAR_PREFIX = "jar:";

    private static void processDirectory(File directory, String packageName, ArrayList<Class<?>> classes) {
        for (File child : directory.listFiles()) {
            final String resource = packageName + DOT + child.getName();
            if (child.isDirectory()) {
                processDirectory(child, resource, classes);
            } else if (resource.endsWith(CLASS_SUFFIX)) {
                final String className = resource.substring(0, resource.length() - CLASS_SUFFIX.length());
                try {
                    classes.add(Class.forName(className));
                }
                catch (ClassNotFoundException ex) {
                    // ignore
                }
            }
        }
    }

    private static void processJar(URL resource, String relativePath, ArrayList<Class<?>> classes) {
        final String jarPath = resource.getPath().replaceFirst("[.]jar[!].*", ".jar").replaceFirst("file:", "");
        try (JarFile jarFile = new JarFile(jarPath)) {
            for (JarEntry entry : Collections.list(jarFile.entries())) {
                final String entryName = entry.getName();
                if (entryName.endsWith(CLASS_SUFFIX) && entryName.startsWith(relativePath) && entryName.length() > (relativePath.length() + 1)) {
                    final String className = entryName.substring(0, entryName.length() - CLASS_SUFFIX.length()).replace(SLASH, DOT);
                    try {
                        classes.add(Class.forName(className));
                    }
                    catch (ClassNotFoundException ex) {
                        // ignore
                    }
                }
            }
        }
        catch (IOException ex) {
            throw new RuntimeException("Unexpected IOException reading JAR File '" + jarPath + "'", ex);
        }
    }

    /**
     * List all classes in a given package and its sub-packages.
     *
     * @param packageName a given packaged to look for classes
     * @return a list of classes
     */
    public static Class<?>[] getClassesForPackage(String packageName) {
        ArrayList<Class<?>> classes = new ArrayList<>();

        final String relPath = packageName.replace(DOT, SLASH);

        // with Maven's Surefire plugin, the SystemClassLoader does neither
        // contain target/classes nor target/test-classes in its classpath;
        // we need to use ClassLoader of our class to load the resources
        //final ClassLoader classLoader = ClassLoader.getSystemClassLoader();
        final ClassLoader classLoader = ClassFinder.class.getClassLoader();

        final URL resource = classLoader.getResource(relPath);
        if (resource == null) {
            throw new RuntimeException("Unexpected problem: No resource for " + relPath);
        }

        if (resource.toString().startsWith(JAR_PREFIX)) {
            processJar(resource, relPath, classes);
        } else {
            processDirectory(new File(resource.getPath()), packageName, classes);
        }

        return classes.toArray(new Class<?>[0]);
    }

    /**
     * Returns descriptors for all properties of the class as it is a bean.
     *
     * @param classWithProperties a class to be analyzed
     * @return an array of <code>PropertyDescriptor</code> objects, or
     * <code>null</code> if the information is to be obtained through the
     * automatic analysis
     * @throws IntrospectionException if an exception occurs during
     * introspection
     */
    public static PropertyDescriptor[] getPropertyDescriptors(Class<?> classWithProperties) throws IntrospectionException {
        return Introspector.getBeanInfo(classWithProperties, Object.class).getPropertyDescriptors();
    }

    /**
     * Set a given property of a given name of a given object to a given value.
     *
     * @param objectWithProperties an object of the property
     * @param propertyName a name of the property
     * @param propertyValue a value of the property to set
     * @return <code>true</code> if successful, <code>false</code> otherwise
     * @throws IntrospectionException if an exception occurs during
     * introspection of the object
     * @throws InvocationTargetException if the underlying method throws an
     * exception
     * @throws IllegalAccessException if this Method object is enforcing Java
     * language access control and the underlying method is inaccessible
     * @throws IllegalArgumentException if the method is an instance method and
     * the specified object argument is not an instance of the class or
     * interface declaring the underlying method (or of a subclass or
     * implementor thereof); if the number of actual and formal parameters
     * differ; if an unwrapping conversion for primitive arguments fails; or if,
     * after possible unwrapping, a parameter value cannot be converted to the
     * corresponding formal parameter type by a method invocation conversion
     */
    public static boolean setProperty(Object objectWithProperties, String propertyName, String propertyValue) throws IntrospectionException, InvocationTargetException, IllegalAccessException, IllegalArgumentException {
        // find the property
        PropertyDescriptor foundPropertyDescriptor = null;
        for (PropertyDescriptor propertyDescriptor : ClassFinder.getPropertyDescriptors(objectWithProperties.getClass())) {
            if (propertyDescriptor.getName().equals(propertyName)) {
                foundPropertyDescriptor = propertyDescriptor;
                break;
            }
        }
        if (foundPropertyDescriptor == null) {
            return false;
        }
        // find its setter method
        final Method propertySetterMethod = foundPropertyDescriptor.getWriteMethod();
        if (propertySetterMethod == null) {
            return false;
        }
        // find its only parameter
        Class<?>[] parameterTypes = propertySetterMethod.getParameterTypes();
        if (parameterTypes.length != 1) {
            return false;
        }
        // fill the parameter with the given value
        Object parameterObject;
        if (parameterTypes[0].equals(String.class)) {
            parameterObject = propertyValue;
        } else if (parameterTypes[0].equals(Byte.class) || parameterTypes[0].equals(byte.class)) {
            parameterObject = Byte.valueOf(propertyValue);
        } else if (parameterTypes[0].equals(Short.class) || parameterTypes[0].equals(short.class)) {
            parameterObject = Short.valueOf(propertyValue);
        } else if (parameterTypes[0].equals(Integer.class) || parameterTypes[0].equals(int.class)) {
            parameterObject = Integer.valueOf(propertyValue);
        } else if (parameterTypes[0].equals(Long.class) || parameterTypes[0].equals(long.class)) {
            parameterObject = Long.valueOf(propertyValue);
        } else if (parameterTypes[0].equals(Float.class) || parameterTypes[0].equals(float.class)) {
            parameterObject = Float.valueOf(propertyValue);
        } else if (parameterTypes[0].equals(Double.class) || parameterTypes[0].equals(double.class)) {
            parameterObject = Double.valueOf(propertyValue);
        } else if (parameterTypes[0].equals(Boolean.class) || parameterTypes[0].equals(boolean.class)) {
            parameterObject = propertyValue.isEmpty()
                    ? Boolean.TRUE
                    : Boolean.valueOf(propertyValue);
        } else {
            return false;
        }
        // call the method
        propertySetterMethod.invoke(objectWithProperties, parameterObject);
        return true;
    }

    public static void main(String[] args) throws IntrospectionException {
        final String packageName = args.length >= 1
                ? args[0]
                : ClassFinder.class.getPackage().getName();
        System.out.println("Classes in package " + packageName + " (the package-name can be set as the first argument):");
        for (Class<?> foundClass : ClassFinder.getClassesForPackage(packageName)) {
            System.out.println("\n" + foundClass.getCanonicalName());
            for (PropertyDescriptor propertyDescriptor : ClassFinder.getPropertyDescriptors(foundClass)) {
                System.out.println("* property " + propertyDescriptor.getName());
                if (propertyDescriptor.getReadMethod() != null) {
                    System.out.println("  " + propertyDescriptor.getReadMethod());
                }
                if (propertyDescriptor.getWriteMethod() != null) {
                    System.out.println("  " + propertyDescriptor.getWriteMethod());
                }
            }
        }
    }

}
