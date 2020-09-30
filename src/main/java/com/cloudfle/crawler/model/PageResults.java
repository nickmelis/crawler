package com.cloudfle.crawler.model;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize (builder = PageResults.Builder.class)
public class PageResults {

  private final URL pageUrl;
  private final Set<URL> links;
  private StaticAssets staticAssets;

  private PageResults(
      Builder builder) {
    this.pageUrl = builder.pageUrl;
    this.links = builder.links;
    this.staticAssets = builder.staticAssets;
  }

  public PageResults(
      URL pageUrl) {
    this.pageUrl = pageUrl;
    this.links = new HashSet<>();
    this.staticAssets = StaticAssets.builder().build();
  }

  public URL getPageUrl() {
    return pageUrl;
  }

  public Set<URL> getLinks() {
    return links;
  }

  public StaticAssets getStaticAssets() {
    return staticAssets;
  }

  /**
   * Creates builder to build {@link PageResults}.
   *
   * @return created builder
   */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * Builder to build {@link PageResults}.
   */
  public static final class Builder {
    private URL pageUrl;
    private Set<URL> links = new HashSet<>();
    private StaticAssets staticAssets;

    private Builder() {
    }

    public Builder withPageUrl(URL pageUrl) {
      this.pageUrl = pageUrl;
      return this;
    }

    public Builder withLinks(Set<URL> links) {
      this.links = links;
      return this;
    }

    public Builder withLink(URL link) {
      this.links.add(link);
      return this;
    }

    public Builder withStaticAssets(StaticAssets staticAssets) {
      this.staticAssets = staticAssets;
      return this;
    }

    public PageResults build() {
      return new PageResults(this);
    }
  }
}
