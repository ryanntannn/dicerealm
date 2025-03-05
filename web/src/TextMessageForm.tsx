import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { Button } from "./components/ui/button";
import { Input } from "./components/ui/input";

export type TextMessageFormProps = {
  onSend: (message: string) => void;
};

const formSchema = z.object({
  message: z.string().max(200, "Message cannot be longer than 200 characters"),
});

export default function TextMessageForm({ onSend }: TextMessageFormProps) {
  const form = useForm({
    resolver: zodResolver(formSchema),
  });

  async function onSubmit(data: z.infer<typeof formSchema>) {
    onSend(data.message);
    form.reset();
  }

  return (
    <form
      className="w-full flex flex-row gap-2"
      onSubmit={form.handleSubmit(onSubmit)}>
      <Input
        placeholder="Send a message..."
        className="flex-grow border border-gray-300 rounded p-2"
        type="text"
        {...form.register("message")}
      />
      <Button type="submit">Send</Button>
    </form>
  );
}
