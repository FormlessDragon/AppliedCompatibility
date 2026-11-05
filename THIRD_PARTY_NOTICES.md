# Third-party source notices

The legacy fluid inventory interfaces and GUI widgets listed in
`docs/legacy-fluid-gui-source-lock.json` are based on
[AE2-UEL/Applied-Energistics-2](https://github.com/AE2-UEL/Applied-Energistics-2),
commit `724ffaebe3a8ba7d2320431acf03a8c2f6345d9e`.

Copyright AlgorithmX2 and the AE2 / AE2-UEL contributors. Implementation
classes use LGPL-3.0-or-later. The `MENetworkChannelChanged` API event uses
the MIT license printed in its source header.
Original headers are retained where present. `LICENSE` contains LGPL version 3;
`COPYING.GPL-3` supplies its incorporated GPL terms.

Local adaptations restore the legacy API on AE2S, preserve the existing
two-argument inventory callback, use the native AE2S label renderer, implement
window-scoped fluid synchronization, and guard capacity arithmetic and no-op
mutations. The source lock records the original imported file hashes, before
these adaptations.
