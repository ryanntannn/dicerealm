import { zodResolver } from "@hookform/resolvers/zod";
import { Player, playerSchema } from "./lib/room-state";
import { useForm } from "react-hook-form";

import { Button } from "@/components/ui/button";
import {
  Form,
  FormControl,
  FormDescription,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "./components/ui/select";
import { useEffect } from "react";

export type CustomizeCharacterFormProps = {
  player: Player;
  onSubmit: (newCharacter: Player) => void;
};

const raceMap = {
  HUMAN: "Human",
  ELF: "Elf",
  DEMON: "Demon",
  DWARF: "Dwarf",
  TIEFLING: "Tiefling",
};

const entityClassMap = {
  WARRIOR: "Warrior",
  WIZARD: "Wizard",
  CLERIC: "Cleric",
  ROGUE: "Rogue",
  RANGER: "Ranger",
};

export default function CustomizeCharacterForm({
  player,
  onSubmit,
}: CustomizeCharacterFormProps) {
  const form = useForm({
    resolver: zodResolver(playerSchema),
    defaultValues: player,
  });

  const { isDirty, isValid } = form.formState;

  useEffect(() => {
    form.reset(player);
  }, [player, form]);

  return (
    <Form {...form}>
      <form
        onSubmit={form.handleSubmit(async (p) => onSubmit(p))}
        className="space-y-6">
        <FormField
          control={form.control}
          name="displayName"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Name</FormLabel>
              <FormControl>
                <Input placeholder="Leroy Jenkins" {...field} />
              </FormControl>
              <FormDescription>
                The full name of your in-game character
              </FormDescription>
              <FormMessage />
            </FormItem>
          )}
        />
        {/* Race dropdown */}
        <FormField
          control={form.control}
          name="race"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Race</FormLabel>
              <FormControl>
                <Select onValueChange={field.onChange} value={field.value}>
                  <SelectTrigger className="w-[180px]">
                    <SelectValue>
                      {raceMap[field.value as keyof typeof raceMap]}
                    </SelectValue>
                  </SelectTrigger>
                  <SelectContent>
                    {Object.keys(raceMap).map((key) => (
                      <SelectItem key={key} value={key}>
                        {raceMap[key as keyof typeof raceMap]}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </FormControl>
              <FormDescription>
                The race of your in-game character
              </FormDescription>
              <FormMessage />
            </FormItem>
          )}
        />

        {/* Class dropdown */}
        <FormField
          control={form.control}
          name="entityClass"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Class</FormLabel>
              <FormControl>
                <Select onValueChange={field.onChange} value={field.value}>
                  <SelectTrigger className="w-[180px]">
                    <SelectValue>
                      {
                        entityClassMap[
                          field.value as keyof typeof entityClassMap
                        ]
                      }
                    </SelectValue>
                  </SelectTrigger>
                  <SelectContent>
                    {Object.keys(entityClassMap).map((key) => (
                      <SelectItem key={key} value={key}>
                        {entityClassMap[key as keyof typeof entityClassMap]}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </FormControl>
              <FormDescription>
                The class of your in-game character
              </FormDescription>
              <FormMessage />
            </FormItem>
          )}
        />
        <Button type="submit" disabled={!isDirty || !isValid}>
          Update Character Sheet
        </Button>
      </form>
    </Form>
  );
}
