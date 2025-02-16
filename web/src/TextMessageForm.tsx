import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import { z } from "zod";

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
      className="w-full flex flex-row gap-2 p-4"
      onSubmit={form.handleSubmit(onSubmit)}>
      <input
        className="flex-grow border border-gray-300 rounded p-2"
        type="text"
        {...form.register("message")}
      />
      <button
        className="bg-gray-100 px-4 rounded border border-gray-300"
        type="submit">
        Send
      </button>
    </form>
  );
}
