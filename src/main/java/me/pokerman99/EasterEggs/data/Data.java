package me.pokerman99.EasterEggs.data;

import java.util.List;;
import java.util.Optional;
import javax.annotation.Generated;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataHolder;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.manipulator.DataManipulatorBuilder;
import org.spongepowered.api.data.manipulator.immutable.common.AbstractImmutableData;
import org.spongepowered.api.data.manipulator.mutable.common.AbstractData;
import org.spongepowered.api.data.merge.MergeFunction;
import org.spongepowered.api.data.persistence.AbstractDataBuilder;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.data.value.immutable.ImmutableListValue;
import org.spongepowered.api.data.value.mutable.ListValue;

@Generated(value = "flavor.pie.generator.data.DataManipulatorGenerator", date = "2018-04-08T19:36:57.729Z")
public class Data extends AbstractData<Data, Data.Immutable> {

    public List<String> eggdata;

    {
        registerGettersAndSetters();
    }

    public Data() {
    }

    public Data(List<String> eggdata) {
        this.eggdata = eggdata;
    }

    @Override
    public void registerGettersAndSetters() {
        registerFieldGetter(Keys.EGGDATA, this::getEggdata);
        registerFieldSetter(Keys.EGGDATA, this::setEggdata);
        registerKeyValue(Keys.EGGDATA, this::eggdata);
    }

    public List<String> getEggdata() {
        return eggdata;
    }

    public void setEggdata(List<String> eggdata) {
        this.eggdata = eggdata;
    }

    public ListValue<String> eggdata() {
        return Sponge.getRegistry().getValueFactory().createListValue(Keys.EGGDATA, eggdata);
    }

    @Override
    public Optional<Data> fill(DataHolder dataHolder, MergeFunction overlap) {
        dataHolder.get(Data.class).ifPresent(that -> {
                Data data = overlap.merge(this, that);
                this.eggdata = data.eggdata;
        });
        return Optional.of(this);
    }

    @Override
    public Optional<Data> from(DataContainer container) {
        return from((DataView) container);
    }

    public Optional<Data> from(DataView container) {
        container.getStringList(Keys.EGGDATA.getQuery()).ifPresent(v -> eggdata = v);
        return Optional.of(this);
    }

    @Override
    public Data copy() {
        return new Data(eggdata);
    }

    @Override
    public Immutable asImmutable() {
        return new Immutable(eggdata);
    }

    @Override
    public int getContentVersion() {
        return 1;
    }

    @Override
    public DataContainer toContainer() {
        return super.toContainer()
                .set(Keys.EGGDATA.getQuery(), eggdata);
    }

    @Generated(value = "flavor.pie.generator.data.DataManipulatorGenerator", date = "2018-04-08T19:36:57.749Z")
    public static class Immutable extends AbstractImmutableData<Immutable, Data> {

        public List<String> eggdata;
        {
            registerGetters();
        }

        Immutable() {
        }

        Immutable(List<String> eggdata) {
            this.eggdata = eggdata;
        }

        @Override
        public void registerGetters() {
            registerFieldGetter(Keys.EGGDATA, this::getEggdata);
            registerKeyValue(Keys.EGGDATA, this::eggdata);
        }

        public List<String> getEggdata() {
            return eggdata;
        }

        public ImmutableListValue<String> eggdata() {
            return Sponge.getRegistry().getValueFactory().createListValue(Keys.EGGDATA, eggdata).asImmutable();
        }

        @Override
        public Data asMutable() {
            return new Data(eggdata);
        }

        @Override
        public int getContentVersion() {
            return 1;
        }

        @Override
        public DataContainer toContainer() {
            return super.toContainer()
                    .set(Keys.EGGDATA.getQuery(), eggdata);
        }

    }

    @Generated(value = "flavor.pie.generator.data.DataManipulatorGenerator", date = "2018-04-08T19:36:57.753Z")
    public static class Builder extends AbstractDataBuilder<Data> implements DataManipulatorBuilder<Data, Immutable> {

        public Builder() {
            super(Data.class, 1);
        }

        @Override
        public Data create() {
            return new Data();
        }

        @Override
        public Optional<Data> createFrom(DataHolder dataHolder) {
            return create().fill(dataHolder);
        }

        @Override
        public Optional<Data> buildContent(DataView container) throws InvalidDataException {
            return create().from(container);
        }

    }
}
