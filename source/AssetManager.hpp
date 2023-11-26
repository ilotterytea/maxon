#pragma once
#include <SFML/Graphics/Texture.hpp>
#include <iostream>
#include <map>
#include <string>

namespace Maxon {
  class AssetManager {
    std::map<std::string, sf::Texture> m_textures;

   public:
    AssetManager() = default;
    ~AssetManager() = default;

    void load_texture(const std::string& file_name) {
      sf::Texture res;

      if (res.loadFromFile(file_name)) {
        this->m_textures[file_name] = res;
        std::cout << "Loaded " << file_name << " file as a texture\n";
      }
    };

    sf::Texture& get_texture(const std::string& file_name) {
      return this->m_textures.at(file_name);
    };

    void dispose_texture(const std::string& file_name){};
  };
}
