# LedgerFlow
Enterprise Multi-Currency Transaction &amp; Settlement Platform

## Module D

The settlement endpoint accepts a CSV file and stores it under the configured settlement input directory before launching `settlementJob`. Each invocation receives a unique run identifier, so the same file may be intentionally processed again. Duplicate `external_tx_id` values are skipped and logged. Business-invalid rows, including missing or inactive accounts, are also skipped and logged; malformed rows are never persisted. A fee discrepancy takes precedence over the audit state, and the audit threshold is evaluated against the converted USD amount.
