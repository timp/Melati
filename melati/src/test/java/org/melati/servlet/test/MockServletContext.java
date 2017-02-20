/**
 * 
 */
package org.melati.servlet.test;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import javax.servlet.*;
import javax.servlet.descriptor.JspConfigDescriptor;

/**
 * @author timp
 * @since 26 Jun 2007
 *
 */
public class MockServletContext implements ServletContext {

  /**
   * Introduced in 2.4
   * @see javax.servlet.ServletContext#getContextPath()
   */
  public String getContextPath() {
    throw new RuntimeException("TODO No one else has ever called this method." +
                               " Do you really want to start now?");
    
  }

  /**
   * 
   */
  public MockServletContext() {
  }

  /** 
   * {@inheritDoc}
   * @see javax.servlet.ServletContext#getAttribute(java.lang.String)
   */
  public Object getAttribute(String name) {
    return null;
  }

  /** 
   * {@inheritDoc}
   * @see javax.servlet.ServletContext#getAttributeNames()
   */
  public Enumeration<String> getAttributeNames() {
    return null;
  }

  /** 
   * {@inheritDoc}
   * @see javax.servlet.ServletContext#getContext(java.lang.String)
   */
  public ServletContext getContext(String uripath) {
    return null;
  }

  /** 
   * {@inheritDoc}
   * @see javax.servlet.ServletContext#getInitParameter(java.lang.String)
   */
  public String getInitParameter(String name) {
    return null;
  }

  /** 
   * {@inheritDoc}
   * @see javax.servlet.ServletContext#getInitParameterNames()
   */
  public Enumeration<String> getInitParameterNames() {
    return null;
  }

  @Override
  public boolean setInitParameter(String s, String s1) {
    return false;
  }

  /** 
   * {@inheritDoc}
   * @see javax.servlet.ServletContext#getMajorVersion()
   */
  public int getMajorVersion() {
    return 0;
  }

  /** 
   * {@inheritDoc}
   * @see javax.servlet.ServletContext#getMimeType(java.lang.String)
   */
  public String getMimeType(String file) {
    return null;
  }

  /** 
   * {@inheritDoc}
   * @see javax.servlet.ServletContext#getMinorVersion()
   */
  public int getMinorVersion() {
    return 0;
  }

  @Override
  public int getEffectiveMajorVersion() {
    return 0;
  }

  @Override
  public int getEffectiveMinorVersion() {
    return 0;
  }

  /** 
   * {@inheritDoc}
   * @see javax.servlet.ServletContext#getNamedDispatcher(java.lang.String)
   */
  public RequestDispatcher getNamedDispatcher(String name) {
    return null;
  }

  /** 
   * {@inheritDoc}
   * @see javax.servlet.ServletContext#getRealPath(java.lang.String)
   */
  public String getRealPath(String path) {
    return "/dist/melati/melati/src/test/webapp";
  }

  /** 
   * {@inheritDoc}
   * @see javax.servlet.ServletContext#getRequestDispatcher(java.lang.String)
   */
  public RequestDispatcher getRequestDispatcher(String path) {
    return null;
  }

  /** 
   * {@inheritDoc}
   * @see javax.servlet.ServletContext#getResource(java.lang.String)
   */
  public URL getResource(String path) throws MalformedURLException {
    return null;
  }

  /** 
   * {@inheritDoc}
   * @see javax.servlet.ServletContext#getResourceAsStream(java.lang.String)
   */
  public InputStream getResourceAsStream(String path) {
    return null;
  }

  /** 
   * {@inheritDoc}
   * @see javax.servlet.ServletContext#getResourcePaths(java.lang.String)
   */
  public Set<String> getResourcePaths(String arg0) {
    return null;
  }

  /** 
   * {@inheritDoc}
   * @see javax.servlet.ServletContext#getServerInfo()
   */
  public String getServerInfo() {
    return null;
  }

  /** 
   * {@inheritDoc}
   * @see javax.servlet.ServletContext#getServlet(java.lang.String)
   */
  public Servlet getServlet(String name) throws ServletException {
    return null;
  }

  /** 
   * {@inheritDoc}
   * @see javax.servlet.ServletContext#getServletContextName()
   */
  public String getServletContextName() {
    return null;
  }

  @Override
  public ServletRegistration.Dynamic addServlet(String s, String s1) {
    return null;
  }

  @Override
  public ServletRegistration.Dynamic addServlet(String s, Servlet servlet) {
    return null;
  }

  @Override
  public ServletRegistration.Dynamic addServlet(String s, Class<? extends Servlet> aClass) {
    return null;
  }

  @Override
  public <T extends Servlet> T createServlet(Class<T> aClass) throws ServletException {
    return null;
  }

  @Override
  public ServletRegistration getServletRegistration(String s) {
    return null;
  }

  @Override
  public Map<String, ? extends ServletRegistration> getServletRegistrations() {
    return null;
  }

  @Override
  public FilterRegistration.Dynamic addFilter(String s, String s1) {
    return null;
  }

  @Override
  public FilterRegistration.Dynamic addFilter(String s, Filter filter) {
    return null;
  }

  @Override
  public FilterRegistration.Dynamic addFilter(String s, Class<? extends Filter> aClass) {
    return null;
  }

  @Override
  public <T extends Filter> T createFilter(Class<T> aClass) throws ServletException {
    return null;
  }

  @Override
  public FilterRegistration getFilterRegistration(String s) {
    return null;
  }

  @Override
  public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
    return null;
  }

  @Override
  public SessionCookieConfig getSessionCookieConfig() {
    return null;
  }

  @Override
  public void setSessionTrackingModes(Set<SessionTrackingMode> set) {

  }

  @Override
  public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
    return null;
  }

  @Override
  public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
    return null;
  }

  @Override
  public void addListener(String s) {

  }

  @Override
  public <T extends EventListener> void addListener(T t) {

  }

  @Override
  public void addListener(Class<? extends EventListener> aClass) {

  }

  @Override
  public <T extends EventListener> T createListener(Class<T> aClass) throws ServletException {
    return null;
  }

  @Override
  public JspConfigDescriptor getJspConfigDescriptor() {
    return null;
  }

  @Override
  public ClassLoader getClassLoader() {
    return null;
  }

  @Override
  public void declareRoles(String... strings) {

  }

  @Override
  public String getVirtualServerName() {
    return null;
  }

  /** 
   * {@inheritDoc}
   * @see javax.servlet.ServletContext#getServletNames()
   */
  public Enumeration<String> getServletNames() {
    return null;
  }

  /** 
   * {@inheritDoc}
   * @see javax.servlet.ServletContext#getServlets()
   */
  public Enumeration<Servlet> getServlets() {
    return null;
  }

  /** 
   * {@inheritDoc}
   * @see javax.servlet.ServletContext#log(java.lang.String)
   */
  public void log(String msg) {
  }

  /** 
   * {@inheritDoc}
   * @see javax.servlet.ServletContext#log(java.lang.Exception, java.lang.String)
   */
  public void log(Exception exception, String msg) {
  }

  /** 
   * {@inheritDoc}
   * @see javax.servlet.ServletContext#log(java.lang.String, java.lang.Throwable)
   */
  public void log(String message, Throwable throwable) {
  }

  /** 
   * {@inheritDoc}
   * @see javax.servlet.ServletContext#removeAttribute(java.lang.String)
   */
  public void removeAttribute(String name) {
  }

  /** 
   * {@inheritDoc}
   * @see javax.servlet.ServletContext#setAttribute(java.lang.String, java.lang.Object)
   */
  public void setAttribute(String name, Object object) {
  }

  Hashtable<String,String> expectations = new Hashtable<String,String>();
  
  /**
   * @param key
   * @param value
   */
  public void expectAndReturn(String key, String value) {
    expectations.put(key, value);
  }

}
