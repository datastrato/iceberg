# Datastrato branches

`ds/*` branches carry test-only builds of Apache Iceberg for Datastrato, such as `ds/1.12.0-rest-encryption`
(Iceberg 1.12.0 with apache/iceberg#13225). They are not releases and are not supported for customers.

Upstream's GitHub Actions workflows are removed on these branches so nothing runs on push or pull request. Build and
test locally with Gradle; a branch that publishes a jar does it deliberately (design: datastrato/loom
`design-doc/loom/test-images/custom-jars-spip.md`).
