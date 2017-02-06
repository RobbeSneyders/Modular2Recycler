# Modular²Recycler

Modular²Recycler is a `RecyclerView.Adapter` that is modular *squared*.

## Design Pattern

This library uses the approach of [Modular design](https://en.wikipedia.org/wiki/Modular_design), in which a system is subdivided into modular, reusable components. It makes use of both the [composition](https://en.wikipedia.org/wiki/Object_composition) and [aggregation](https://en.wikipedia.org/wiki/Object_composition#Aggregation) pattern. A detailed explanation about the architecture of this library can be read [here](https://robbesneyders.github.io/Modular²Recycler).

The ² in Modular²Recycler denotes the modularity of the adapter on two separate levels.

### First Level

Instead of creating one huge adapter to populate a `RecyclerView` with our data, we create an __AdapterModule__ for each different viewtype. The composition (or better: aggregation) of these __AdapterModules__ is handled by this library.

<img src="docs/art/Modular_level_1.png" alt="Modular level 1" width="300">  
Figure 1: Aggregation of __AdapterModules__ into ModularAdapter.

### Second level

On the second level, these __AdapterModules__ are modular themselves. They consist of a fundament: the class __AdapterModule__, which they should inherit from. Reusable components can be added to this fundament to add certain functionalities. These reusable components are offered by this library in the form of __Plugins__ which are implemented as `Interfaces`.

<img src="docs/art/Modular_level_2.png" alt="Modular level 2" width="250">  
Figure 2: Composition of __Plugins__ into __AdapterModule__.

#### Available plugins

The plugins offered by this library at the moment are:
- OnItemClick
- OnItemLongClick
- Headers
- Swipe to remove/archive/any action
    - Undo
- Drag and drop
    - Stay within section

## Dependencies

This libary can be added to your project by using [JitPack](https://jitpack.io/).

Add Jitpack in your root build.gradle at the end of repositories:

```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

Add this library as dependency to your project:

```
dependencies {
        compile 'com.github.RobbeSneyders:Modular²Recycler:v1.0'
}
```

## How to use

This library does a lot of the necessary work for you. Just follow these steps:  
*Example based on available example app.*

__1.__ For each ViewType  
- Create an item by extending __ModularItem__
- Create a module by extending __AdapterModule__

*Pokemon & PokemonModule*

```
public class Pokemon extends ModularItem {
    public int id;
    public String name;
    public String type = "";
    public Drawable icon;
}

class PokemonModule extends AdapterModule<PokemonViewHolder, Pokemon> {

    public PokemonModule(ModularAdapter adapter) {
        super(adapter);
    }

    @Override
    public PokemonViewHolder onCreateViewHolder(ViewGroup parent) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_row, parent, false);
        return new PokemonViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PokemonViewHolder viewHolder, Pokemon pokemon) {
        viewHolder.vText.setText(pokemon.name);
        viewHolder.vIcon.setImageDrawable(pokemon.icon);
    }

    public static class PokemonViewHolder extends RecyclerView.ViewHolder {
        public ImageView vIcon;
        public TextView vText;

        public PokemonViewHolder(View view) {
            super(view);
            vText = (TextView) view.findViewById(R.id.textView);
            vIcon = (ImageView) view.findViewById(R.id.imageView);
        }
    }    
}
```

*Header & HeaderModule*

```
public class Header extends ModularItem {
    String name;

    public Header(String name) {
        this.name = name;
        // set isHeader = true to make ModularAdapter recognize this as a header class.
        isHeader = true;
    }
}

class HeaderModule extends AdapterModule<HeaderModule.HeaderViewHolder, Header> {

    HeaderModule(ModularAdapter adapter) {
        super(adapter);
    }

    @Override
    public HeaderViewHolder onCreateViewHolder(ViewGroup parent) {
        View headerView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_header, parent, false);
        return new HeaderViewHolder(headerView);
    }

    @Override
    public void onBindViewHolder(HeaderViewHolder viewHolder, Header header) {
        viewHolder.vText.setText(header.name);
    }

    static class HeaderViewHolder extends ViewHolder {
        TextView vText;

        HeaderViewHolder(View view) {
            super(view);
            vText = (TextView) view.findViewById(R.id.textView);
        }
    }
}

```

__2.__ Add the desired functionality to your module by implementing the corresponding __plugins__.

```
public class PokemonModule extends AdapterModule<PokemonViewHolder, Pokemon>
    implements ItemClickPlugin, ItemLongClickPlugin {

    // AdapterModule Methods
    ...

    @Override
    public void onItemClicked(Pokemon pokemon) {
        // Item clicked
    }

    @Override
    public boolean onItemLongClicked(Pokemon pokemon) {
        // Item long clicked
        return true;
    }
```

__4.__ Create an instance of the ModularAdapter in your `Activity`

*There is no need to extend the ModularAdapter class!*

```
List<Pokemon> pokemonList = Pokedex.getAllPokemonAlphabetic();
List<ModularItem> list = addHeaders(pokemonList);

adapter = new ModularAdapterBuilder<>(recyclerView, list)
        .setSwipeLeft(Color.RED, ResourcesCompat.getDrawable(getResources(), R.drawable.ic_delete_white_24dp, null))
        .setSwipeRight(Color.GREEN, ResourcesCompat.getDrawable(getResources(), R.drawable.ic_cloud_upload_white_24dp, null))
        .build();
```

__5.__ Create an instance of the __AdapterModules__ you want to add and pass in the created instance of __ModularAdapter__

```
new PokemonModule(adapter);
new HeaderModule(adapter);
```

## Example App

PokéApp is an example app to demonstrate the use of this library.  
- [Code](app)  
- [Available on Play Store](https://play.google.com/store/apps/details?id=com.cuttingedge.pokeApp).

[![UndoRecycler](http://i.imgur.com/jFQTroq.gif)](http://imgur.com/jFQTroq) [![UndoRecyclerAlphabetic](http://i.imgur.com/5bgXPR2.gif)](http://imgur.com/5bgXPR2)

## Other apps using this library

[Swipe Shortcuts Widget](https://play.google.com/store/apps/details?id=com.cuttingedge.swipeshortcuts)

## Contributing

Contributions in any way are greatly appreciated.

## License

Code released under the [Apache license](LICENSE).


