#include "Graph.h"

#include <map>
#include <vector>
#include <set>
#include <iostream>
#include <fstream>
#include <string>
#include <cassert>

Graph::Graph(uint32_t nodes) {
    n = 0;
    m = 0;
    m_distinct = 0;
    for(int i = 0; i < nodes; i++) {
        addNode();
    }
    name = "graph";
}

uint32_t Graph::getNoVertices() {
    return n;
}

uint32_t Graph::getNoEdges() {
    return m;
}

uint32_t Graph::getNoDistinctEdges() {
    return m_distinct;
}

bool Graph::addNode() {
    bool ans = addNode(large_id);
    if(ans) large_id++;
    return ans;
}

bool Graph::addNode(uint32_t node) {
    auto value = adj.find(node);
    if(value != adj.end()) return false;
    std::set<uint32_t> new_adj;
    adj[node] = new_adj;
    nbh_size[node] = 0;
    degree[node] = 0;
    nbh_set.insert({0, node});
    degree_set.insert({0, node});
    n++;
    return true;
}

bool Graph::removeNode(uint32_t node) {
    auto value = adj.find(node);
    if(value == adj.end()) return false;
    while(adj[node].size()) {
        if(!removeEdge(node, (*adj[node].begin()), -1)) return false;
    }
    adj.erase(node);
    nbh_size.erase(node);
    degree.erase(node);
    nbh_set.erase({0, node});
    degree_set.erase({0, node});
    n--;
    return true;
}

void Graph::addEdge(uint32_t from, uint32_t to, uint32_t count) {
    assert(count > 0);
    adj[from].insert(to);
    adj[to].insert(from);
    auto value = edge_count.find({std::min(from, to), std::max(from, to)});
    if(value != edge_count.end()) {
        edge_set.erase({edge_count[{std::min(from, to), std::max(from, to)}], {std::min(from, to), std::max(from, to)}});
        edge_count[{std::min(from, to), std::max(from, to)}] += count;
        edge_set.insert({edge_count[{std::min(from, to), std::max(from, to)}], {std::min(from, to), std::max(from, to)}});
    } else {
        edge_count[{std::min(from, to), std::max(from, to)}] = count;
        edge_set.insert({count, {std::min(from, to), std::max(from, to)}});
        nbh_set.erase({nbh_size[from], from});
        nbh_set.erase({nbh_size[to], to});
        nbh_size[from]++;
        nbh_size[to]++;
        nbh_set.insert({nbh_size[from], from});
        nbh_set.insert({nbh_size[to], to});
        m_distinct++;
    }
    degree_set.erase({degree[from], from});
    degree_set.erase({degree[to], to});
    degree[from] += count;
    degree[to] += count;
    degree_set.insert({degree[from], from});
    degree_set.insert({degree[to], to});
    m += count;
}

bool Graph::removeEdge(uint32_t from, uint32_t to, uint32_t count) {
    auto value = edge_count.find({std::min(from, to), std::max(from, to)});
    if(value == edge_count.end()) return false;
    uint32_t edges = edge_count[{std::min(from, to), std::max(from, to)}];
    assert(count <= edges);
    if(edges == count || count == -1) {
        nbh_set.erase({nbh_size[from], from});
        nbh_set.erase({nbh_size[to], to});
        nbh_size[from]--;
        nbh_size[to]--;
        nbh_set.insert({nbh_size[from], from});
        nbh_set.insert({nbh_size[to], to});
        degree_set.erase({degree[from], from});
        degree_set.erase({degree[to], to});
        degree[from] -= edges;
        degree[to] -= edges;
        degree_set.insert({degree[from], from});
        degree_set.insert({degree[to], to});
        edge_count.erase({std::min(from, to), std::max(from, to)});
        edge_set.erase({edges, {std::min(from, to), std::max(from, to)}});
        adj[from].erase(to);
        adj[to].erase(from);
        m_distinct--;
        m -= edges;
    } else {
        degree_set.erase({degree[from], from});
        degree_set.erase({degree[to], to});
        degree[from] -= count;
        degree[to] -= count;
        degree_set.insert({degree[from], from});
        degree_set.insert({degree[to], to});
        edge_set.erase({edges, {std::min(from, to), std::max(from, to)}});
        edge_count[{std::min(from, to), std::max(from, to)}] -= count;
        edge_set.insert({edges-count, {std::min(from, to), std::max(from, to)}});
        m -= count;
    }
    return true;
}

uint32_t Graph::edgeCount(uint32_t from, uint32_t to) {
    auto value = edge_count.find({std::min(from, to), std::max(from, to)});
    if(value == edge_count.end()) return 0;
    return edge_count[{std::min(from, to), std::max(from, to)}];
}

bool Graph::readFromFile(std::string fileName) {
    std::ifstream in(fileName.c_str());
    std::set<uint32_t> nodes;
    std::vector<std::pair<uint32_t, uint32_t>> edges;
    std::string line;
    while(getline(in,line)) {
        if(!line.size()) continue;
        if(line[0] == '%' || line[0] == '#') continue;
        std::pair<uint32_t, uint32_t> edge = {0,0};
        bool after_space = 0;
        for(int i = 0; i < line.size(); i++) {
            if(line[i] == ' ') {
                after_space = 1;
            } else if(!after_space) {
                edge.first *= 10;
                edge.first += line[i]-'0';
            } else if(after_space) {
                edge.second *= 10;
                edge.second += line[i]-'0';
            }
        }
        nodes.insert(edge.first);
        nodes.insert(edge.second);
        edges.push_back(edge);
    }
    for(int i = 0; i < nodes.size(); i++) if(!addNode()) return false;
    for(auto [from, to] : edges) addEdge(from, to, 1);
    size_t found = fileName.find_last_of('/');
    if (found == std::string::npos) {
        name = fileName.substr(0, fileName.size()-6);
    } else {
        name = fileName.substr(found+1, fileName.size()-found-1-6);
    }
    return true;
}

std::ostream& operator<<(std::ostream& os, Graph& graph) {
    os << "####################\n";
    os << "Graph: " << graph.name << "\n";
    os << "#nodes: " << graph.n << "\n";
    os << "#edges: " << graph.m << "\n";
    os << "#distinct edges: " << graph.m_distinct << "\n";
    if(graph.extended_info) {
        os << "Node edge structure:\n";
        for(auto [key, value] : graph.adj) {
            os << "    Node: " << key << ", nbh_size: " << graph.nbh_size[key] << ", degree: " << graph.degree[key] << "\n";
            os << "    Edges:\n";
            for(auto num : value) {
                os << "        Node: " << num << ", edge_count: " << graph.edge_count[{std::min(key, num), std::max(key, num)}] << "\n";
            }
        }
    }
    os << "####################\n";
    return os;
}