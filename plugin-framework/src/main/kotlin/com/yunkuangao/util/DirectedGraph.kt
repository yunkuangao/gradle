package com.yunkuangao.util

import java.util.*

class DirectedGraph<V> {

    var neighbors: MutableMap<V, MutableList<V>> = HashMap()
        private set

    fun addVertex(vertex: V) {
        if (containsVertex(vertex)) {
            return
        }
        neighbors[vertex] = mutableListOf()
    }

    fun containsVertex(vertex: V): Boolean {
        return neighbors.containsKey(vertex)
    }

    fun removeVertex(vertex: V) {
        neighbors.remove(vertex)
    }

    fun addEdge(from: V, to: V) {
        addVertex(from)
        addVertex(to)
        neighbors[from]!!.add(to)
    }

    fun removeEdge(from: V, to: V) {
        require(containsVertex(from)) { "Nonexistent vertex $from" }
        require(containsVertex(to)) { "Nonexistent vertex $to" }
        neighbors[from]!!.remove(to)
    }

    fun getNeighbors(vertex: V): MutableList<V> {
        return if (containsVertex(vertex)) neighbors[vertex]!! else mutableListOf()
    }

    fun outDegree(): MutableMap<V, Int> {
        val result: MutableMap<V, Int> = HashMap()
        for (vertex in neighbors.keys) {
            result[vertex] = neighbors[vertex]!!.size
        }
        return result
    }

    fun inDegree(): MutableMap<V, Int> {
        val result: MutableMap<V, Int> = HashMap()
        for (vertex in neighbors.keys) {
            result[vertex] = 0 // all in-degrees are 0
        }
        for (from in neighbors.keys) {
            for (to in neighbors[from]!!) {
                result[to] = result[to]!! + 1 // increment in-degree
            }
        }
        return result
    }

    fun topologicalSort(): MutableList<V> {
        val degree = inDegree()

        // determine all vertices with zero in-degree
        val zeroVertices = Stack<V>() // stack as good as any here
        for (v in degree.keys) {
            if (degree[v] == 0) {
                zeroVertices.push(v)
            }
        }

        val result: MutableList<V> = mutableListOf()
        while (!zeroVertices.isEmpty()) {
            val vertex = zeroVertices.pop()
            result.add(vertex)
            for (neighbor in neighbors[vertex]!!) {
                degree[neighbor] = degree[neighbor]!! - 1
                if (degree[neighbor] == 0) {
                    zeroVertices.push(neighbor)
                }
            }
        }

        return if (result.size != neighbors.size) mutableListOf()
        else result
    }

    fun reverseTopologicalSort(): MutableList<V> {
        return topologicalSort().reversed().toMutableList()
    }

    fun isDag(): Boolean {
        return topologicalSort() != null
    }

    override fun toString(): String {
        val sb = StringBuilder()
        for (vertex in neighbors.keys) {
            sb.append("\n   ").append(vertex).append(" -> ").append(neighbors[vertex])
        }
        return sb.toString()
    }

}