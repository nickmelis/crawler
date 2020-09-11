package com.sedna.crawler.model;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize (builder = StaticAssets.Builder.class)
public class StaticAssets {
  private final Set<URL> images;
  private final Set<URL> css;
  private final Set<URL> js;

  private StaticAssets(
      Builder builder) {
    this.images = builder.images;
    this.css = builder.css;
    this.js = builder.js;
  }

  public Set<URL> getImages() {
    return images;
  }

  public Set<URL> getCss() {
    return css;
  }

  public Set<URL> getJs() {
    return js;
  }

  /**
   * Creates builder to build {@link StaticAssets}.
   *
   * @return created builder
   */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * Builder to build {@link StaticAssets}.
   */
  public static final class Builder {
    private Set<URL> images = new HashSet<>();
    private Set<URL> css = new HashSet<>();
    private Set<URL> js = new HashSet<>();

    private Builder() {
    }

    public Builder withImages(Set<URL> images) {
      this.images = images;
      return this;
    }

    public Builder withCss(Set<URL> css) {
      this.css = css;
      return this;
    }

    public Builder withJs(Set<URL> js) {
      this.js = js;
      return this;
    }

    public StaticAssets build() {
      return new StaticAssets(this);
    }
  }
}