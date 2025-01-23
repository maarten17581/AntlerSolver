#ifndef AS_GRAPH_H
#define AS_GRAPH_H

#include <map>
#include <vector>
#include <set>
#include <iostream>
#include <fstream>
#include <string>
#include <cassert>

struct Graph {

    std::map<uint32_t, std::set<uint32_t>> adj;
    std::map<std::pair<uint32_t, uint32_t>, uint32_t> edge_count;
    std::map<uint32_t, uint32_t> nbh_size;
    std::map<uint32_t, uint32_t> degree;
    std::set<std::pair<uint32_t, std::pair<uint32_t, uint32_t>>> edge_set;
    std::set<std::pair<uint32_t, uint32_t>> nbh_set;
    std::set<std::pair<uint32_t, uint32_t>> degree_set;
    uint32_t n;
    uint32_t large_id = 0;
    uint32_t m;
    uint32_t m_distinct;
    std::string name;
    bool extended_info = 0;


    explicit Graph() : n(0), m(0), m_distinct(0), name("graph") {}
    explicit Graph(uint32_t n);

    uint32_t getNoVertices();
    uint32_t getNoEdges();
    uint32_t getNoDistinctEdges();

    bool addNode();
    bool addNode(uint32_t node);
    bool removeNode(uint32_t node);

    void addEdge(uint32_t from, uint32_t to, uint32_t count);
    bool removeEdge(uint32_t from, uint32_t to, uint32_t count);
    uint32_t edgeCount(uint32_t from, uint32_t to);

    bool readFromFile(std::string fileName);

    friend std::ostream& operator<<(std::ostream& os, Graph& graph);
};

#endif //AS_GRAPH_H