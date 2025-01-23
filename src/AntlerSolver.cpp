#include <string>
#include <iostream>
#include <filesystem>

#include "Graph.h"

int main(int argc, char *argv[]) {

    if (argc < 3) {
        std::cout << "Usage: AntlerSolver <graphFile/graphDir> <outputDir>" << std::endl;
        return 0;
    }
    
    std::string graphFile {argv[1]};
    std::string ouputDir {argv[2]};
    std::filesystem::path path(graphFile);
    std::error_code ec;
    if (std::filesystem::is_directory(path, ec)) { 
        for (const auto & entry : std::filesystem::directory_iterator(graphFile)) {
            std::cout << entry.path() << std::endl;
            Graph g;
            g.readFromFile(entry.path());
            if(g.name == "nd_7_10") {
                std::cout << g;
            }
        }
    } else {
        std::cout << graphFile << std::endl;
        Graph g;
        std::cout << g.readFromFile(graphFile) << "\n";
        std::cout << g;

        std::set<uint32_t> fvs;

        while(g.n && (*g.degree_set.begin()).first <= 2) {
            uint32_t node = (*g.degree_set.begin()).second;
            //std::cout << node << " with degree " << (*g.degree_set.begin()).first << " removed\n";
            if((*g.degree_set.begin()).first == 2) {
                if(g.nbh_size[node] == 1) {
                    fvs.insert((*g.adj[node].begin()));
                    g.removeNode((*g.adj[node].begin()));
                } else {
                    g.addEdge((*g.adj[node].begin()), (*(++g.adj[node].begin())), 1);
                }
            }
            if(!g.removeNode(node)) {
                std::cout << "Could not remove " << node << "\n";
                break;
            }
            while(g.n && (*(--g.edge_set.end())).first > 2) {
                //std::cout << "Edge " << (*(--g.edge_set.end())).second.first << " - " << (*(--g.edge_set.end())).second.second << " has degree " << (*(--g.edge_set.end())).first << "\n";
                g.removeEdge((*(--g.edge_set.end())).second.first, (*(--g.edge_set.end())).second.second, (*(--g.edge_set.end())).first-2);
            }
        }
        std::cout << g;
        std::cout << "FVS:\n";
        for(uint32_t num : fvs) {
            std::cout << num << "\n";
        }
    }

}
