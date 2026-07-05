package appeng.api.networking;

/**
 * Old AE visitor ABI for walking grid nodes.
 */
public interface IGridVisitor {

    /**
     * Called for each visited node.
     *
     * @param node current old AE grid node facade.
     * @return true to continue visiting nodes beyond this node.
     */
    boolean visitNode(IGridNode node);
}
