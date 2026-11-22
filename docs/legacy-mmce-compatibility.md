# Legacy fluid, GUI and MMCE addon compatibility

These adapters target Applied Energistics 2 Supergiant (AE2S) on Minecraft
1.12.2 / Cleanroom / Java 25. They restore behavior behind existing legacy
`appeng` APIs used by MMCE and addons such as MMCE Complement.

## Restored behavior

- `AEFluidInventory` implements Forge fluid capabilities, simulation, per-slot
  capacity, fill/drain, NBT persistence and server-side change callbacks.
  Capacity arithmetic retains long stored amounts without integer overflow.
- Legacy containers bind player inventory, support transfers and phantom slots,
  run the vanilla listener lifecycle, check interaction distance and synchronize
  held stacks. Fluid widgets render contents and send configuration edits.
- Fluid synchronization preserves quantities, tags and explicit empty slots.
  Server handlers schedule work on the server thread, check the open window and
  interaction validity, and restrict fluid configuration edits to
  `ContainerFluidConfigurable`. Packet discriminators are registered on both
  physical sides; client access is isolated behind the client proxy.
- MMCE's native pattern decode cache grows to the actual pattern inventory size,
  so addon providers with more than the original 36 slots can be enumerated.

`InvOperation` restores the legacy `EXTRACT`, `INSERT` and `SET` enum constants;
the earlier AppliedCompatibility lowercase field names remain as aliases. The
two-argument fluid callback remains supported through a default detailed callback.

`verifyPermissions` uses AE2S's native location permission check. The target AE2S
API has no equivalent of AE2UEL's per-action security service, so this adapter
does not implement separate legacy permission categories.

## Optional shared MMCE nodes

An MMCE addon can implement `MMCEProxyNodeHost` to supply its legacy proxy's
native managed node during the base constructor, or use `MMCEGridNodeAccess`
to replace the temporary bridge node before the first world tick. Replacing a
different node destroys the old endpoint. The first-tick callback reads the
current node, and an already ready shared node is not created twice.

The addon configures the shared node's listeners and services. The MMCE bridge
forwards the tile's create, save, load and destroy lifecycle without replacing
the addon's callbacks. These interfaces do not require MMCE Complement to be
installed.

## Verification

The normal upstream command is `./gradlew compileJava test` with Java 25.
The added tests cover fluid simulation, capacities, partial-fill callbacks,
long amounts, fluid tags, invalid requests, NBT replacement, callback
compatibility, packet snapshots, decoder reuse and invalid packet sizes.
Callback tests run in Cleanroom's server thread group so the effective side
check is exercised.

Runtime acceptance should cover a client and dedicated server, including
actual fluid container clicks, opening/closing GUIs, an addon provider with
more than 36 pattern slots, and shared node save/reload. For Cleanroom 0.4.0
support, use the released `0.4.0` loader itself; another version or an alpha
build does not substitute for that acceptance.

Source origins and licenses are recorded in `THIRD_PARTY_NOTICES.md` and
`legacy-fluid-gui-source-lock.json`. The Unimined build and project identity
are retained.
