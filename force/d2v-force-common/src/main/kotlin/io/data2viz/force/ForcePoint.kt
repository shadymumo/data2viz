package io.data2viz.force

import io.data2viz.geom.Point
import io.data2viz.geom.point

/**
 * Creates a new positioning force along the y-axis towards the given position y.
 * If y is not specified, it defaults to 0.
 */
class ForcePoint<D> internal constructor(): Force<D> {

    private val defaultPoint = point(0, 0)

    /**
     * Sets the y-coordinate accessor to the specified function, re-evaluates the y-accessor for each node.
     * If y is not specified, returns the current y-accessor, which defaults to { .0 }
     * The y-accessor is invoked for each node in the simulation, being passed the node and its zero-based index.
     * The resulting number is then stored internally, such that the target y-coordinate of each node is only recomputed
     * when the force is initialized or when this method is called with a new y, and not on every application of the force.
     */
    var pointGet: ForceNode<D>.() -> Point = { defaultPoint }
        set(value) {
            field = value
            assignNodes(nodes)
        }

    /**
     * Sets the strength accessor to the specified function, re-evaluates the strength accessor for each node.
     * The strength determines how much to increment the node’s y-velocity: (y - node.y) × strength.
     * For example, a value of 0.1 indicates that the node should move a tenth of the way from its current y-position
     * to the target y-position with each application. Higher values moves nodes more quickly to the target position,
     * often at the expense of other forces or constraints. A value outside the range [0,1] is not recommended.
     *
     * If strength is not specified, returns the current strength accessor, which defaults to { 0.1 }.
     *
     * The strength accessor is invoked for each node in the simulation, being passed the node and its zero-based index.
     * The resulting number is then stored internally, such that the strength of each node is only recomputed when the
     * force is initialized or when this method is called with a new strength, and not on every application of the force.
     */
    var strengthGet: ForceNode<D>.() -> Double = { 0.1 }
        set(value) {
            field = value
            assignNodes(nodes)
        }

    private var nodes: List<ForceNode<D>> = listOf()
    private val strengths = mutableListOf<Double>()
    private val xz = mutableListOf<Double>()
    private val yz = mutableListOf<Double>()

    override fun assignNodes(nodes: List<ForceNode<D>>) {
        this.nodes = nodes

        xz.clear()
        yz.clear()
        strengths.clear()

        nodes.forEach {
            val point = it.pointGet()
            xz.add(point.x)
            yz.add(point.y)
            strengths.add(it.strengthGet())
        }
    }

    override fun applyForceToNodes(alpha: Double) {
        nodes.forEachIndexed { index, node ->
            node.vx += (xz[index] - node.x) * strengths[index] * alpha
            node.vy += (yz[index] - node.y) * strengths[index] * alpha
        }
    }
}