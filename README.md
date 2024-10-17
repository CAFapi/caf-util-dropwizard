# caf-util-dropwizard

This project provides Dropwizard utility code.

## caf-substitutor-dropwizard

This module provides a `CafConfigSubstitutor` class that combines secret lookup: 

```yaml
password: ${secret:MY_PASSWORD:-mysecretpassword}
```

and environment variable lookup capabilities:

```yaml
applicationName: ${MY_DATABASE_APPNAME:-MyDbAppName}
```

It can be enabled in a Dropwizard project like this:

```java
@Override
public void initialize(final Bootstrap<MyAppConfiguration> bootstrap)
{
    bootstrap.setConfigurationSourceProvider(new SubstitutingSourceProvider(
            bootstrap.getConfigurationSourceProvider(),
            new CafConfigSubstitutor(false, true)));

    super.initialize(bootstrap);
}
```